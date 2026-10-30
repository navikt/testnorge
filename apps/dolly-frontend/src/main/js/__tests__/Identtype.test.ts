import { describe, expect, it } from 'vitest'
import { getIdenttypeFraIdent, getMasterForIdenttype } from '@/utils/identtype'

describe('getIdenttypeFraIdent', () => {
	it.each([
		['01010112345', 'FNR'],
		['41010112345', 'DNR'],
		['01230112345', 'NPID'],
	] as const)('should identify %s as %s', (ident, expected) => {
		expect(getIdenttypeFraIdent(ident)).toBe(expected)
	})

	it.each([null, undefined, '', '1234567890', '0101011234x'])(
		'should return null for invalid ident %s',
		(ident) => {
			expect(getIdenttypeFraIdent(ident)).toBeNull()
		},
	)
})

describe('getMasterForIdenttype', () => {
	it.each([
		['NPID', 'PDL'],
		['DNR', 'FREG'],
		['FNR', 'FREG'],
		[null, 'FREG'],
		[undefined, 'FREG'],
	] as const)('should map identtype %s to master %s', (identtype, expected) => {
		expect(getMasterForIdenttype(identtype)).toBe(expected)
	})
})
