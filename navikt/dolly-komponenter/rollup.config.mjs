import peerDepsExternal from 'rollup-plugin-peer-deps-external'
import { nodeResolve } from '@rollup/plugin-node-resolve'
import commonjs from '@rollup/plugin-commonjs'
import typescript from 'rollup-plugin-typescript2'
import image from '@rollup/plugin-image'
import postcss from 'rollup-plugin-postcss'
import NpmImport from 'less-plugin-npm-import'

import packageJson from "./package.json" assert { type: "json" };

export default {
	input: 'src/index.ts',
	output: [
		{
			file: packageJson.main,
			format: 'cjs',
			sourcemap: true
		},
		{
			file: packageJson.module,
			format: 'esm',
			sourcemap: true
		}
	],
	plugins: [
		peerDepsExternal(),
		nodeResolve({ moduleDirectories: ['node_modules', 'lib'] }),
		commonjs(),
		image(),
		// visualizer(),
		postcss({
			use: [
				[
					'less',
					{
						plugins: [new NpmImport({ prefix: '~' })]
					}
				]
			]
		}),
		typescript({ useTsconfigDeclarationDir: true })
	]
}