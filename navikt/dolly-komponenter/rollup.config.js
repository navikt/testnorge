import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import terser from '@rollup/plugin-terser';
import peerDepsExternal from 'rollup-plugin-peer-deps-external';
import image from '@rollup/plugin-image';
import typescript from '@rollup/plugin-typescript';
import css from 'rollup-plugin-import-css';
import {createRequire} from 'module';

const require = createRequire(import.meta.url);
const packageJson = require('./package.json', {assert: {type: 'json'}});

export default [
    {
        input: 'src/index.ts',
        output: [
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
            css(),
            typescript({tsconfig: './tsconfig.json', jsx: 'react'}),
            terser(),
            image(),
        ],
        external: ['react', 'react-dom'],
    },
];