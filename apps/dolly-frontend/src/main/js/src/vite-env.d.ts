interface ImportMetaEnv {
	readonly APP_VERSION: string
	readonly COMMIT_HASH: string
	readonly GIT_BRANCH: string
}

interface ImportMeta {
	readonly env: ImportMetaEnv
}
