import { describe, expect, it } from 'vitest'
import { getOrgMiljoer } from '@/utils/OrgUtils'
import { OrganisasjonForvalterData } from '@/service/services/organisasjonforvalter/types'

const makeOrgData = (miljoer: Record<string, { organisasjonsnummer: string }>) =>
	miljoer as unknown as OrganisasjonForvalterData

describe('getOrgMiljoer', () => {
	it('shouldReturnBothEnvironmentsWhenBothExist', () => {
		const org = makeOrgData({
			q1: { organisasjonsnummer: '123456789' },
			q2: { organisasjonsnummer: '123456789' },
		})

		const result = getOrgMiljoer(org)

		expect(result.sort()).toEqual(['q1', 'q2'])
	})

	it('shouldReturnOnlyQ1WhenQ2IsMissing', () => {
		const org = makeOrgData({
			q1: { organisasjonsnummer: '123456789' },
		})

		const result = getOrgMiljoer(org)

		expect(result).toEqual(['q1'])
	})

	it('shouldReturnEmptyArrayForUndefined', () => {
		expect(getOrgMiljoer(undefined)).toEqual([])
	})

	it('shouldExcludeEntriesWithoutOrganisasjonsnummer', () => {
		const org = makeOrgData({
			q1: { organisasjonsnummer: '123456789' },
			q2: { organisasjonsnummer: '' },
		})

		const result = getOrgMiljoer(org)

		expect(result).toEqual(['q1'])
	})

	it('shouldHandleMultipleEnvironments', () => {
		const org = makeOrgData({
			q1: { organisasjonsnummer: '123456789' },
			q2: { organisasjonsnummer: '123456789' },
			q4: { organisasjonsnummer: '123456789' },
		})

		const result = getOrgMiljoer(org)

		expect(result.sort()).toEqual(['q1', 'q2', 'q4'])
	})
})
