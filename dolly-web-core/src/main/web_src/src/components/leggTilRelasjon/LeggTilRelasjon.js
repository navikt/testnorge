import React from 'react'
import * as Yup from 'yup'
import { Formik, Form } from 'formik'
import Knapp from 'nav-frontend-knapper'
import _difference from 'lodash/difference'
import _get from 'lodash/get'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { DollyApi } from '~/service/Api'
import { Partner } from './relasjonstype/Partner'
import { Barn } from './relasjonstype/Barn'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { requiredString, ifPresent, ifKeyHasValue, messages } from '~/utils/YupValidations'

import Panel from '~/components/ui/panel/Panel'

export const LeggTilRelasjon = ({
	environments,
	hovedIdent,
	identInfo,
	closeModal,
	getBestillinger,
	gruppeId
}) => {
	const harAlleredePartner = harPartner(identInfo[hovedIdent])
	const partnere = muligePartnere(identInfo, hovedIdent)

	const onSubmit = async values => {
		await DollyApi.createRelasjon(hovedIdent, values)
		getBestillinger(gruppeId)
		return closeModal()
	}

	const initialValues = {
		environments: environments,
		tpsf: {
			relasjoner: {
				partner: [],
				barn: []
			}
		}
	}
	return (
		<div className="add-relasjon">
			<Formik
				onSubmit={onSubmit}
				onReset={initialValues}
				validationSchema={validation}
				initialValues={initialValues}
				enableReinitialize
			>
				{props => {
					const brukteIdenter = []
					console.log('props :', props)
					// props.values.tpsf.relasjoner.partner
					// 	.map(ident => ident.ident)
					// 	.concat(props.values.tpsf.relasjoner.barn.map(ident => ident.ident))
					return (
						<Form autoComplete="off">
							<div className="add-relasjon">
								<div>
									<Panel heading="Partner" iconType="relasjoner" startOpen>
										{harAlleredePartner ? (
											<AlertStripeInfo>
												Personen har allerede en partner, og du får derfor ikke legge til flere.
											</AlertStripeInfo>
										) : (
											<Partner
												props={props}
												valgbareIdenter={lagOptions(partnere, brukteIdenter)}
											/>
										)}
									</Panel>
								</div>
								<div>
									<Panel heading="Barn (adoptert)" iconType="relasjoner" startOpen>
										<Barn
											props={props}
											valgbareIdenter={lagOptions(muligeBarn(identInfo, hovedIdent), brukteIdenter)}
										/>
									</Panel>
								</div>
								<div className="action-knapper">
									<Knapp type="standard" onClick={closeModal}>
										Avbryt
									</Knapp>
									<Knapp type="hoved" htmlType="submit">
										Legg til relasjon(er)
									</Knapp>
								</div>
							</div>
						</Form>
					)
				}}
			</Formik>
		</div>
	)
}

const muligeBarn = (identInfo, hovedIdent) => {
	//Denne må testes nå vi får hovedperson med foreldre
	//Hvis det kun blir adopsjon -> trenger vi sjekke MOR og FAR?
	return Object.keys(identInfo).filter(ident => {
		if (ident === hovedIdent) return false
		if (!identInfo[ident].relasjoner) return true

		const counter = identInfo[ident].relasjoner.reduce((acc, relasjon) => {
			if (relasjon.relasjonTypeNavn === 'MOR' || relasjon.relasjonTypeNavn === 'FAR') {
				// ADOPSJON?
				acc++
			}
			return acc
		}, 0)
		return counter < 2
	})
}

const muligePartnere = (identInfo, hovedIdent) => {
	// Per nå kan ikke bruker legge til en partner som allerede har en annen partner
	return Object.keys(identInfo).filter(ident => {
		if (ident === hovedIdent) return false
		return !harPartner(identInfo[ident])
	})
}

const harPartner = identInfo => {
	return (
		identInfo.relasjoner &&
		identInfo.relasjoner.some(relasjon => {
			return relasjon.relasjonTypeNavn === 'PARTNER'
		})
	)
}

const lagOptions = (tilgjengelige, brukte) => {
	return _difference(tilgjengelige, brukte).reduce((acc, ident) => {
		return [...acc, { value: ident, label: ident, isDisabled: true }]
	}, [])
}

const validation = () =>
	Yup.object({
		environments: Yup.array().required(messages.required),
		tpsf: Yup.object({
			relasjoner: Yup.object({
				partner: Yup.array().of(
					Yup.object({
						ident: Yup.string().required(messages.required),
						sivilstander: Yup.array().of(
							Yup.object({
								sivilstand: Yup.string()
									.test('is-not-ugift', 'Ugyldig sivilstand for partner', value => value !== 'UGIF')
									.required('Feltet er påkrevd'),
								sivilstandRegdato: Yup.string()
									.test(
										'is-before-last',
										'Dato må være før tidligere forhold (nyeste forhold settes først)',
										function validDate(val) {
											console.log('this :', this)
											const values = this.options.context
											const path = this.options.path
											const thisDate = _get(values, path)
											const partnerIndex = path.charAt(path.indexOf('[') + 1)
											const sivilstander =
												values.tpsf.relasjoner.partnere[partnerIndex].sivilstander
											const forholdIndex = _findIndex(sivilstander, ['sivilstandRegdato', thisDate])
											if (forholdIndex === 0) return true
											return isBefore(thisDate, sivilstander[forholdIndex - 1].sivilstandRegdato)
										}
									)
									.required('Feltet er påkrevd')
							})
						)
					})
				),
				barn: Yup.array().of(
					Yup.object({
						ident: Yup.string().required(messages.required)
					})
				)
			})
		})
	})
