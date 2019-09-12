import miljoeStatusSelector, {
	avvikStatus,
	countAntallIdenterOpprettet
} from '../MiljoeStatusSelector'

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

	describe('miljoeStatusSelector()', () => {
		const bestillingMockMedTpsfSuccessEnvs = {
			tpsfStatus: [
				{
					statusMelding: 'OK',
					environmentIdents: {
						t5: ['19106129722', '14028733245'],
						t11: ['19106129722']
					}
				}
			]
		}

		const bestillingMockMedSuccessRegister = {
			tpsfStatus: [
				{
					statusMelding: 'OK',
					environmentIdents: {
						t5: ['19106129722', '14028733245']
					}
				}
			],
			krrStubStatus: [
				{
					statusMelding: 'OK',
					identer: ['19106129722']
				}
			],
			sigrunStubStatus: [
				{
					statusMelding: 'OK',
					identer: ['19106129722']
				}
			],
			arenaforvalterStatus: [
				{
					status: 'OK',
					envIdent: { t5: ['19106129722'] }
				}
			],
			aaregStatus: [
				{
					statusMelding: 'OK',
					environmentIdentsForhold: {
						t5: { '19106129722': ['arbforhold 1'] }
					}
				}
			],
			instdataStatus: [
				{
					statusMelding: 'OK',
					environmentIdentsForhold: {
						t5: { '19106129722': ['opphold 1'] }
					}
				}
			],
			pdlforvalterStatus: {
				falskIdentitet: [
					{
						statusMelding: 'OK',
						identer: ['19106129722']
					}
				]
			}
		}

		const bestillingMockMedFailedRegister = {
			tpsfStatus: [
				{
					statusMelding: 'Feilet',
					environmentIdents: {
						t5: ['19106129722', '14028733245']
					}
				}
			],
			krrStubStatus: [
				{
					statusMelding: 'Feilet',
					identer: ['19106129722']
				}
			],
			sigrunStubStatus: [
				{
					statusMelding: 'Feilet',
					identer: ['19106129722']
				}
			],
			arenaforvalterStatus: [
				{
					status: 'Feilet',
					envIdent: { t5: ['19106129722'] }
				}
			],
			aaregStatus: [
				{
					statusMelding: 'Feilet',
					environmentIdentsForhold: {
						t5: { '19106129722': ['arbforhold 1'] }
					}
				}
			],
			instdataStatus: [
				{
					statusMelding: 'Feilet',
					environmentIdentsForhold: {
						t5: { '19106129722': ['opphold 1'] }
					}
				}
			],
			pdlforvalterStatus: {
				falskIdentitet: [
					{
						statusMelding: 'Feilet',
						identer: ['19106129722']
					}
				]
			}
		}

		it('skal returnere null hvis bestillingen er undefined', () => {
			expect(miljoeStatusSelector(undefined)).toBe(null)
		})

		it('skal ha property "finnesFeilmelding" satt til true dersom det finnes avvik', () => {
			const medAvvik = {
				finnesFeilmelding: true
			}
			expect(miljoeStatusSelector(bestillingMockMedFailedRegister)).toMatchObject(medAvvik)
		})

		it('skal returnere successEnvs t5 og t11', () => {
			const miljoeObj = {
				successEnvs: ['t5', 't11']
			}
			expect(miljoeStatusSelector(bestillingMockMedTpsfSuccessEnvs)).toMatchObject(miljoeObj)
		})

		it('skal returnere mange successEnvs', () => {
			const miljoeObjMangeSuccess = {
				successEnvs: ['t5', 'Krr-stub', 'Sigrun-stub', 'Pdl-forvalter', 'Inst', 'AAREG', 'Arena']
			}
			expect(miljoeStatusSelector(bestillingMockMedSuccessRegister)).toMatchObject(
				miljoeObjMangeSuccess
			)
		})

		it('skal returnere mange failedEnvs', () => {
			const miljoeObjMangeFail = {
				failedEnvs: ['t5', 'Krr-stub', 'Sigrun-stub', 'Pdl-forvalter', 'Inst', 'AAREG', 'Arena']
			}
			expect(miljoeStatusSelector(bestillingMockMedFailedRegister)).toMatchObject(
				miljoeObjMangeFail
			)
		})

		it('skal returnere tpsf kombi', () => {
			const bestillingMockMedKombiTpsf = {
				tpsfStatus: [
					{
						statusMelding: 'OK',
						environmentIdents: {
							t5: ['19106129722', '14028733245'],
							t12: ['14028733245']
						}
					},
					{
						statusMelding: 'Feilet',
						environmentIdents: {
							t11: ['19106129722', '14028733245'],
							t12: ['19106129722']
						}
					}
				]
			}
			const miljoeObjKombiTpsf = {
				successEnvs: ['t5'],
				failedEnvs: ['t11', 't12']
			}
			expect(miljoeStatusSelector(bestillingMockMedKombiTpsf)).toMatchObject(miljoeObjKombiTpsf)
		})

		it('skal returnere inst kombi', () => {
			const bestillingMockMedKombiInst = {
				instdataStatus: [
					{
						statusMelding: 'OK',
						environmentIdentsForhold: {
							q0: { '19106129722': ['opphold 1'] },
							q2: { '19106129722': ['opphold 1'] }
						}
					},
					{
						statusMelding: 'Feilet',
						environmentIdentsForhold: {
							t5: { '19106129722': ['opphold 1'] }
						}
					}
				]
			}
			const miljoeObjKombiInst = {
				avvikEnvs: ['Inst']
			}
			expect(miljoeStatusSelector(bestillingMockMedKombiInst)).toMatchObject(miljoeObjKombiInst)
		})

		it('skal returnere pdlf kombi', () => {
			const bestillingMockMedKombiPdlf = {
				pdlforvalterStatus: {
					falskIdentitet: [
						{
							statusMelding: 'Feilet',
							identer: ['19106129722']
						}
					],
					kontaktinfoDoedsbo: [
						{
							statusMelding: 'OK',
							identer: ['19106129722']
						}
					],
					utenlandsid: [
						{
							statusMelding: 'Feilet',
							identer: ['19106129722']
						}
					]
				}
			}
			const miljoeObjKombiPdlf = {
				failedEnvs: ['Pdl-forvalter']
				//Burde vært avvikEnvs: ['Pdl-forvalter']?
			}
			expect(miljoeStatusSelector(bestillingMockMedKombiPdlf)).toMatchObject(miljoeObjKombiPdlf)
		})

		it('skal returnere aareg kombi', () => {
			const bestillingMockMedKombiAareg = {
				aaregStatus: [
					{
						statusMelding: 'OK',
						environmentIdentsForhold: {
							t6: { '19106129722': ['arbforhold 1'] }
						}
					},
					{
						statusMelding: 'Feilet',
						environmentIdentsForhold: {
							t5: { '19106129722': ['arbforhold 1'] }
						}
					}
				]
			}
			const miljoeObjKombiAareg = {
				avvikEnvs: ['AAREG']
				//Burde vært avvikEnvs: ['Pdl-forvalter']?
			}
			expect(miljoeStatusSelector(bestillingMockMedKombiAareg)).toMatchObject(miljoeObjKombiAareg)
		})

		it('skal returnere pdlf overall fail', () => {
			const bestillingMockMedPdlfFeil = {
				pdlforvalterStatus: {
					pdlForvalter: [
						{
							statusMelding: 'Feilet',
							identer: ['19106129722']
						}
					]
				}
			}
			const miljoeObjPdlfFeil = {
				failedEnvs: ['Pdl-forvalter']
			}
			expect(miljoeStatusSelector(bestillingMockMedPdlfFeil)).toMatchObject(miljoeObjPdlfFeil)
		})
	})
})
