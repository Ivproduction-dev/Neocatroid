// Collab Firestore rules tests (Firebase emulator, NOT runnable offline).
//
// Setup once (needs network):
//   npm install -g firebase-tools
//   firebase login
//   firebase init emulators   # enable Firestore emulator, port 8080
//   cd catroid && npm install --no-save @firebase/rules-unit-testing
//
// Run:
//   firebase emulators:exec --only firestore "npx mocha collab-rules.test.js --timeout 10000"
//
// The same file is the executable specification of every security invariant:
// чужой presence, самоповышение роли, повторный claim invite, чужие локи.

const assert = require("assert");
const fs = require("fs");
const {
    initializeTestEnvironment,
    assertFails,
    assertSucceeds,
} = require("@firebase/rules-unit-testing");

const SID = "ABC123";
const OWNER = "uid-owner";
const EDITOR = "uid-editor";
const OUTSIDER = "uid-outsider";

function doc(db, path) {
    return db.doc("collabSessions/" + SID + path);
}

describe("collab rules", () => {
    let env;

    before(async () => {
        env = await initializeTestEnvironment({
            projectId: "collab-test",
            firestore: {
                rules: fs.readFileSync("collab-firestore.rules", "utf8"),
                host: "127.0.0.1",
                port: 8080,
            },
        });
    });

    after(async () => {
        await env.cleanup();
    });

    beforeEach(async () => {
        await env.clearFirestore();
        await env.withSecurityRulesDisabled(async (ctx) => {
            const db = ctx.firestore();
            await doc(db, "/meta/meta").set({
                ownerUid: OWNER, ownerName: "Host", projectName: "Game",
                closed: false, createdAt: 1,
            });
            await doc(db, "/members/" + OWNER).set({
                role: "host", githubUsername: "h", colorHue: 1, name: "Host", joinedAt: 1,
            });
            await doc(db, "/members/" + EDITOR).set({
                role: "editor", githubUsername: "e", colorHue: 2, name: "Editor", joinedAt: 2,
            });
            await doc(db, "/invites/111111").set({
                role: "editor", expiresAt: Date.now() + 3600000, usedBy: "",
            });
            await doc(db, "/invites/222222").set({
                role: "editor", expiresAt: Date.now() - 1000, usedBy: "",
            });
            await doc(db, "/invites/333333").set({
                role: "editor", expiresAt: Date.now() + 3600000, usedBy: EDITOR,
            });
        });
    });

    function authed(uid) {
        return env.authenticatedContext(uid).firestore();
    }

    function unauthed() {
        return env.unauthenticatedContext().firestore();
    }

    it("presence: чужой presence писать нельзя, свой можно", async () => {
        const mine = { name: "Editor", colorHue: 2, role: "editor", sceneId: "", spriteId: "", tab: "sprites", detail: "" };
        await assertSucceeds(doc(authed(EDITOR), "/presence/" + EDITOR).set(mine));
        await assertFails(doc(authed(EDITOR), "/presence/" + OWNER).set(mine));
        await assertFails(doc(authed(OUTSIDER), "/presence/" + OUTSIDER).set(mine));
        await assertFails(doc(unauthed(), "/presence/" + EDITOR).set(mine));
    });

    it("presence: читать могут только участники", async () => {
        await assertFails(doc(authed(OUTSIDER), "/presence/" + EDITOR).get());
    });

    it("roles: самоповышение запрещено", async () => {
        await assertFails(
            doc(authed(EDITOR), "/members/" + EDITOR).update({ role: "host" })
        );
    });

    it("roles: редактор не кикает других", async () => {
        await assertFails(doc(authed(EDITOR), "/members/" + OWNER).delete());
    });

    it("roles: владелец понижает, кикает чужого, но не удаляет себя", async () => {
        await assertSucceeds(
            doc(authed(OWNER), "/members/" + EDITOR).update({ role: "viewer" })
        );
        await assertSucceeds(doc(authed(OWNER), "/members/" + EDITOR).delete());
        await assertFails(doc(authed(OWNER), "/members/" + OWNER).delete());
    });

    it("roles: гость не создаёт себе membership", async () => {
        await assertFails(
            doc(authed(OUTSIDER), "/members/" + OUTSIDER).set({
                role: "editor", githubUsername: "x", colorHue: 0, name: "X", joinedAt: 1,
            })
        );
    });

    it("roles: участник читает чужую роль, чужак — нет", async () => {
        await assertSucceeds(doc(authed(EDITOR), "/members/" + OWNER).get());
        await assertSucceeds(doc(authed(OWNER), "/members/" + EDITOR).get());
        await assertFails(doc(authed(OUTSIDER), "/members/" + OWNER).get());
        await assertFails(doc(unauthed(), "/members/" + OWNER).get());
    });

    it("invites: валидный claim проходит, повторный/просроченный/подмена — нет", async () => {
        await assertSucceeds(doc(authed(OUTSIDER), "/invites/111111").update({ usedBy: OUTSIDER }));
        await assertFails(doc(authed(OUTSIDER), "/invites/333333").update({ usedBy: OUTSIDER }));
        await assertFails(doc(authed(OUTSIDER), "/invites/222222").update({ usedBy: OUTSIDER }));
        await assertFails(
            doc(authed(OUTSIDER), "/invites/111111").update({ role: "host", usedBy: OUTSIDER })
        );
        await assertFails(
            doc(authed(OUTSIDER), "/invites/111111").update({ usedBy: EDITOR })
        );
    });

    it("invites: в закрытую комнату claim отклоняется", async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/meta/meta").update({ closed: true });
        });
        await assertFails(doc(authed(OUTSIDER), "/invites/111111").update({ usedBy: OUTSIDER }));
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/meta/meta").update({ closed: false });
        });
        await assertSucceeds(doc(authed(OUTSIDER), "/invites/111111").update({ usedBy: OUTSIDER }));
    });

    it("locks: чужой uid писать нельзя, свой можно", async () => {
        const own = { uid: EDITOR, name: "Editor", colorHue: 2, at: Date.now() };
        await assertSucceeds(doc(authed(EDITOR), "/locks/script1").set(own));
        await assertFails(
            doc(authed(EDITOR), "/locks/script2").set(
                { uid: OWNER, name: "Host", colorHue: 1, at: Date.now() })
        );
        await assertFails(
            doc(authed(OUTSIDER), "/locks/script1").set(
                { uid: OUTSIDER, name: "Out", colorHue: 3, at: Date.now() })
        );
    });

    it("locks: удалять чужой может только владелец", async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/locks/script1").set(
                { uid: EDITOR, name: "Editor", colorHue: 2, at: Date.now() });
        });
        await assertFails(doc(authed(OUTSIDER), "/locks/script1").delete());
        await assertSucceeds(doc(authed(EDITOR), "/locks/script1").delete());

        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/locks/script1").set(
                { uid: EDITOR, name: "Editor", colorHue: 2, at: Date.now() });
        });
        await assertSucceeds(doc(authed(OWNER), "/locks/script1").delete());
    });

    it("locks: читать могут только участники", async () => {
        await assertFails(doc(authed(OUTSIDER), "/locks/anything").get());
    });

    it("meta: чужой не правит и не удаляет, создать с чужим ownerUid нельзя", async () => {
        await assertFails(doc(authed(EDITOR), "/meta/meta").update({ closed: true }));
        await assertFails(doc(authed(EDITOR), "/meta/meta").delete());
        await assertFails(
            env.authenticatedContext(OUTSIDER).firestore()
                .doc("collabSessions/NEWSES/meta/meta")
                .set({ ownerUid: OWNER, ownerName: "x", projectName: "p", closed: false, createdAt: 1 })
        );
    });

    const PATCH = {
        fromUid: EDITOR, fromName: "Editor", at: 1, summary: "s",
        codeChunks: 1, media: [],
    };

    it("patches: создать может только участник от своего имени", async () => {
        await assertSucceeds(doc(authed(EDITOR), "/patches/p1").set(PATCH));
        await assertFails(
            doc(authed(EDITOR), "/patches/p2").set({ ...PATCH, fromUid: OWNER })
        );
        await assertFails(doc(authed(OUTSIDER), "/patches/p3").set({ ...PATCH, fromUid: OUTSIDER }));
    });

    it("patches: update запрещён, delete — автор или владелец", async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/patches/p1").set(PATCH);
        });
        await assertFails(doc(authed(EDITOR), "/patches/p1").update({ summary: "x" }));
        await assertFails(doc(authed(OWNER), "/patches/p1").update({ summary: "x" }));
        await assertSucceeds(doc(authed(EDITOR), "/patches/p1").delete());
    });

    it("patches: чужой патч удаляет только владелец", async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/patches/p1").set(PATCH);
        });
        await assertSucceeds(doc(authed(OWNER), "/patches/p1").delete());
    });

    it("patches/chunks: чанки — только автор патча, читать — участники", async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/patches/p1").set(PATCH);
        });
        const chunk = { kind: "code", ref: "", index: 0, data: "abc" };
        await assertSucceeds(doc(authed(EDITOR), "/patches/p1/chunks/code_0").set(chunk));
        await assertFails(doc(authed(OWNER), "/patches/p1/chunks/code_1").set(chunk));
        await assertFails(doc(authed(OUTSIDER), "/patches/p1/chunks/code_0").get());
        await assertSucceeds(doc(authed(OWNER), "/patches/p1/chunks/code_0").get());
    });

    it("states: писать — только владелец, удалять — любой участник, читать — участники", async () => {
        const state = { ...PATCH, fromUid: OWNER, fromName: "Host" };
        await assertFails(doc(authed(EDITOR), "/states/s1").set(state));
        await assertSucceeds(doc(authed(OWNER), "/states/s1").set(state));
        await assertSucceeds(doc(authed(EDITOR), "/states/s1").get());
        await assertFails(doc(authed(OUTSIDER), "/states/s1").get());
        await assertSucceeds(doc(authed(EDITOR), "/states/s1").delete());
    });

    it("states/chunks: только владелец", async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await doc(ctx.firestore(), "/states/s1").set({ ...PATCH, fromUid: OWNER });
        });
        const chunk = { kind: "code", ref: "", index: 0, data: "abc" };
        await assertFails(doc(authed(EDITOR), "/states/s1/chunks/code_0").set(chunk));
        await assertSucceeds(doc(authed(OWNER), "/states/s1/chunks/code_0").set(chunk));
    });

    it("snapshotRequests: создать — сам участник, читать/удалять — владелец", async () => {
        const req = { uid: EDITOR, name: "Editor", at: 1 };
        await assertSucceeds(doc(authed(EDITOR), "/snapshotRequests/" + EDITOR).set(req));
        await assertFails(
            doc(authed(EDITOR), "/snapshotRequests/" + OWNER).set({ ...req, uid: OWNER })
        );
        await assertFails(doc(authed(EDITOR), "/snapshotRequests/" + EDITOR).get());
        await assertFails(doc(authed(OUTSIDER), "/snapshotRequests/" + OUTSIDER).set(
            { uid: OUTSIDER, name: "Out", at: 1 }));
        await assertSucceeds(doc(authed(OWNER), "/snapshotRequests/" + EDITOR).delete());
    });
});
