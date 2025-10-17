import { cleanup } from '@testing-library/react'
import * as matchers from '@testing-library/jest-dom/matchers'

import { test as testBase } from 'vitest'
import { worker } from './__tests__/mocks/browser.js'

export const dollyTest = testBase.extend({
	worker: [
		async ({}, use) => {
			await worker.start()

			await use(worker)

			worker.stop()
		},
		{
			auto: true,
		},
	],
})

expect.extend(matchers)

afterEach(() => {
	cleanup()
})
