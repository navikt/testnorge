import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgrPlugin from 'vite-plugin-svgr'
import path from 'path'

/** @type {import('vite').UserConfig} */
export default defineConfig({
	build: {
		outDir: 'build',
		loader: { '.js': 'jsx' },
	},
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src'),
		},
	},
	server: {
		proxy: {
			'/api': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/oauth2/authorization/aad': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/oauth2/authorization/idporten': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/login/oauth2/code/aad': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/login/oauth2/code/idporten': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/logout': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/oauth2/logout': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/session/ping': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/session/user': {
				target: 'http://localhost:8020',
				secure: false,
			},
			'/testnav-organisasjon-faste-data-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-tps-messaging-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-adresse-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-joark-dokument-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-inntektstub-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-norg2-proxy/norg2': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/tps-forvalteren-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-brregstub-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-arena-forvalteren-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-krrstub-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-inst-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-sigrunstub-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-aaregister-proxy/': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-pensjon-testdata-facade-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/dolly-backend/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnorge-profil-api/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-varslinger-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-organisasjon-forvalter/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-organisasjon-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-miljoer-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/udi-stub/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-person-search-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-person-organisasjon-tilgang-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-pdl-forvalter/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-bruker-service/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-kontoregister-person-proxy/api/system': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-skjermingsregister-proxy/api': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
			'/testnav-dokarkiv-proxy': {
				target: 'http://localhost:8020',
				changeOrigin: true,
				secure: false,
			},
		},
		port: 3000,
	},
	plugins: [react(), viteTsconfigPaths(), svgrPlugin()],
	// define: {
	// 	VITE_APP_VERSION: JSON.stringify(process.env.npm_package_version),
	// 	VITE_BRANCH_NAME: execSync('git rev-parse --abbrev-ref HEAD').toString().trimEnd(),
	// 	VITE_COMMIT_HASH: execSync('git rev-parse HEAD').toString().trimEnd(),
	// },
})
