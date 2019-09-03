import mss, { avvikStatus, countAntallIdenterOpprettet } from '../MiljoeStatusSelector'

describe('MiljoeStatusSelector.js', () => {
	describe('avvikStatus()', () => {
		const billettStatus = {
			tpsfStatus: [
				{
					statusMelding: 'OK'
				}
			],
			aaregStatus: [
				{
					statusMelding: 'OK'
				}
			],
			arenaforvalterStatus: [
				{
					status: 'OK'
				}
			],
			krrStubStatus: [
				{
					statusMelding: 'OK'
				}
			],
			sigrunStubStatus: [
				{
					statusMelding: 'OK'
				}
			],
			instdataStatus: [
				{
					statusMelding: 'OK'
				}
			],
			pdlforvalterStatus: {
				pdlForvalter: [
					{
						statusMelding: 'OK'
					}
				]
			}
		}

		it('skal returnere en bool', () => {
			expect(typeof avvikStatus({})).toBe('boolean')
		})

		it('skal ha defaultvalue false', () => {
			expect(avvikStatus({})).toBe(false)
		})

		it('skal returnere true dersom objektet har en propertynavn "feil"', () => {
			expect(avvikStatus({ feil: 'anyvalue' })).toBe(true)
		})

		it('skal returnere false dersom alle "statusMelding" attributter er "OK"', () => {
			expect(avvikStatus(billettStatus)).toBe(false)
		})

		it('skal returnere true dersom en av statusmeldingene ikke er "OK"', () => {
			const notOk1 = Object.assign({}, billettStatus, { tpsfStatus: [{ statusMelding: 'feiler' }] })
			const notOk2 = Object.assign({}, billettStatus, {
				aaregStatus: [{ statusMelding: 'feiler' }]
			})
			const notOk3 = Object.assign({}, billettStatus, {
				arenaforvalterStatus: [{ status: 'feiler' }]
			})
			const notOk4 = Object.assign({}, billettStatus, {
				krrStubStatus: [{ statusMelding: 'feiler' }]
			})
			const notOk5 = Object.assign({}, billettStatus, {
				sigrunStubStatus: [{ statusMelding: 'feiler' }]
			})
			const notOk6 = Object.assign({}, billettStatus, {
				instdataStatus: [{ statusMelding: 'feiler' }]
			})
			const notOk7 = Object.assign({}, billettStatus, {
				pdlforvalterStatus: {
					pdlForvalter: [
						{
							statusMelding: 'feiler'
						}
					]
				}
			})

			expect(avvikStatus(notOk1)).toBe(true)
			expect(avvikStatus(notOk2)).toBe(true)
			expect(avvikStatus(notOk3)).toBe(true)
			expect(avvikStatus(notOk4)).toBe(true)
			expect(avvikStatus(notOk5)).toBe(true)
			expect(avvikStatus(notOk6)).toBe(true)
			expect(avvikStatus(notOk7)).toBe(true)
		})
	})

	describe('countAntallIdenterOpprettet()', () => {
		const bestillingMock = {
			tpsfStatus: [
				{
					environmentIdents: {
						t5: ['19106129722', '14028733245']
					}
				}
			]
		}

		const bestillingMockMedDuplikat = {
			tpsfStatus: [
				{
					environmentIdents: {
						t5: ['19106129722', '14028733245'],
						t11: ['19106129722']
					}
				}
			]
		}

		it('skal returnere 0 hvis "tpsfstatus" property ikke finnes', () => {
			expect(countAntallIdenterOpprettet({})).toBe(0)
		})

		it('skal returnere antall identer opprettet', () => {
			expect(countAntallIdenterOpprettet(bestillingMock)).toBe(2)
		})

		it('skal ikke telle duplikater dersom person er opprettet i flere miljøer', () => {
			expect(countAntallIdenterOpprettet(bestillingMockMedDuplikat)).toBe(2)
		})
	})
})
