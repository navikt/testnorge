#!/usr/bin/env node

const fs = require('fs')
const path = require('path')

const buildDir = path.join(__dirname, 'build', 'assets')

console.log('🔍 Checking source maps in build directory...\n')

if (!fs.existsSync(buildDir)) {
	console.error('❌ Build directory not found. Run `npm run build` first.')
	process.exit(1)
}

const files = fs.readdirSync(buildDir)
const jsFiles = files.filter((f) => f.endsWith('.js'))
const mapFiles = files.filter((f) => f.endsWith('.js.map'))

console.log(`📦 Found ${jsFiles.length} JavaScript files`)
console.log(`🗺️  Found ${mapFiles.length} source map files\n`)

if (mapFiles.length === 0) {
	console.error('❌ No source maps found! Check vite.config.js sourcemap setting.')
	process.exit(1)
}

let allGood = true

jsFiles.forEach((jsFile) => {
	const jsPath = path.join(buildDir, jsFile)
	const mapPath = path.join(buildDir, jsFile + '.map')
	const content = fs.readFileSync(jsPath, 'utf-8')

	if (!fs.existsSync(mapPath)) {
		console.error(`❌ Missing source map for ${jsFile}`)
		allGood = false
		return
	}

	if (!content.includes('//# sourceMappingURL=')) {
		console.error(`❌ ${jsFile} doesn't reference its source map`)
		allGood = false
		return
	}

	const mapContent = JSON.parse(fs.readFileSync(mapPath, 'utf-8'))

	if (!mapContent.sources || mapContent.sources.length === 0) {
		console.error(`❌ ${jsFile}.map has no sources`)
		allGood = false
		return
	}

	console.log(`✅ ${jsFile}`)
	console.log(`   → Map file: ${jsFile}.map`)
	console.log(`   → Sources: ${mapContent.sources.length} files`)
	console.log(`   → Has source content: ${mapContent.sourcesContent ? 'Yes' : 'No'}`)

	const sampleSources = mapContent.sources.slice(0, 3).map((s) => {
		return s.replace(/^\.\.\/\.\.\//, '').replace(/^\.\.\//, '')
	})
	console.log(`   → Sample files: ${sampleSources.join(', ')}`)
	console.log('')
})

console.log('\n' + '='.repeat(60))

if (allGood) {
	console.log('✅ All source maps are properly configured!')
	console.log('\nTo test in production:')
	console.log('1. Deploy the build folder with .map files')
	console.log('2. Open the app in a browser')
	console.log('3. Open DevTools Console')
	console.log('4. Click any error stack trace')
	console.log('5. You should see original TypeScript/JSX file names\n')
} else {
	console.error('❌ Some source maps have issues. Check the output above.')
	process.exit(1)
}
