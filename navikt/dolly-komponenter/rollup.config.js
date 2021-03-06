import peerDepsExternal from 'rollup-plugin-peer-deps-external';
import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import typescript from 'rollup-plugin-typescript2';
import image from '@rollup/plugin-image';
import postcss from 'rollup-plugin-postcss';
import NpmImport from 'less-plugin-npm-import';
import { visualizer } from 'rollup-plugin-visualizer';
const packageJson = require('./package.json');

export default {
  input: 'src/index.ts',
  output: [
    {
      file: packageJson.main,
      format: 'cjs',
      sourcemap: true,
    },
    {
      file: packageJson.module,
      format: 'esm',
      sourcemap: true,
    },
  ],
  plugins: [
    peerDepsExternal(),
    resolve(),
    commonjs(),
    image(),
    // visualizer(),
    postcss({
      use: [
        [
          'less',
          {
            plugins: [new NpmImport({ prefix: '~' })],
          },
        ],
      ],
    }),
    typescript({ useTsconfigDeclarationDir: true }),
  ],
};
