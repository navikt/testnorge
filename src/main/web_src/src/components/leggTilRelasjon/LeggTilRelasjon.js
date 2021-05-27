import React from 'react'
import * as Yup from 'yup'
import { Formik, Form, ErrorMessage } from 'formik'
import _difference from 'lodash/difference'
import _get from 'lodash/get'
import { DollyApi } from '~/service/Api'
import { Partner } from './relasjonstype/Partner'
import { Barn } from './relasjonstype/Barn'
import { messages } from '~/utils/YupValidations'
import { sivilstander } from '~/components/fagsystem/tpsf/form/validation'
import { validate } from '~/utils/YupValidations'
import NavButton from '~/components/ui/button/NavButton/NavButton'

import Panel from '~/components/ui/panel/Panel'

export const LeggTilRelasjon = ({
	environments,
	hovedIdent,
	identInfo,
	closeModal,
	getBestillinger,
	gruppeId
}) => {
	const onSubmit = async values => {
		await DollyApi.createRelasjon(hovedIdent, values)
		getBestillinger(gruppeId)
	}

	const initialValues = {
		environments,
		tpsf: {
			relasjoner: {
				partnere: [],
				barn: []
			}
		}
	}
	return (
		<div className="add-relasjon">
			<Formik
				onSubmit={onSubmit}
				validate={async values => await validate(values, validation)}
				initialValues={initialValues}
			>
				{formikBag => {
					return (
						<Form autoComplete="off">
							<div className="add-relasjon">
								<Panel heading="Partner" iconType="partner" startOpen>
									<Partner
										lagOptions={lagOptions}
										identInfo={identInfo}
										hovedIdent={hovedIdent}
										formikBag={formikBag}
									/>
								</Panel>
								<Panel heading="Barn" iconType="barn" startOpen>
									<Barn
										formikBag={formikBag}
										lagOptions={lagOptions}
										identInfo={identInfo}
										hovedIdent={hovedIdent}
									/>
								</Panel>
								{formikBag.values.tpsf.relasjoner.partnere.length < 1 &&
									formikBag.values.tpsf.relasjoner.barn.length < 1 && (
										<ErrorMessage
											name="tpsf.relasjoner.partnere"
											className="error-message"
											component="div"
										/>
									)}
								<div className="action-knapper">
									<NavButton type="fare" onClick={closeModal}>
										Avbryt
									</NavButton>
									<NavButton type="hoved" htmlType="submit">
										Legg til relasjon(er)
									</NavButton>
								</div>
							</div>
						</Form>
					)
				}}
			</Formik>
		</div>
	)
}

const lagOptions = (identer, identInfo) =>
	identer.reduce((acc, ident) => {
		const { fornavn, etternavn } = identInfo[ident]
		return [...acc, { value: ident, label: `${ident} - ${fornavn} ${etternavn}` }]
	}, [])

const validation = Yup.object({
	environments: Yup.array().required(messages.required),
	tpsf: Yup.object({
		relasjoner: Yup.object({
			partnere: Yup.mixed().when('$tpsf.relasjoner.partnere', {
				is: v => v.length > 0,
				then: Yup.array().of(
					Yup.object({
						ident: Yup.string()
							.test(
								'partner-is-not-already-used',
								'Denne personen er allerede brukt som partner',
								function validDate(val) {
									const values = this.options.context
									const path = this.options.path
									const partnerIdent = _get(values, path)
									const antallPartnerSammeIdent = antallIdenter(
										values,
										path,
										partnerIdent,
										'partnere'
									)
									return antallPartnerSammeIdent < 1
								}
							)
							.required(messages.required),
						sivilstander: sivilstander
					})
				),
				otherwise: Yup.mixed().when('$tpsf.relasjoner.barn', {
					is: v => v.length < 1,
					then: Yup.string().required('Legg til partner eller barn')
				})
			}),
			barn: Yup.array().of(
				Yup.object({
					ident: Yup.string()
						.test(
							'is-not-already-used',
							'Denne personen er allerede brukt som partner eller barn',
							function validDate(val) {
								const values = this.options.context
								const path = this.options.path
								const barnIdent = _get(values, path)
								const antallBarnSammeIdent = antallIdenter(values, path, barnIdent, 'barn')
								const partnere = values.tpsf.relasjoner.partnere.map(partner => partner.ident)
								return antallBarnSammeIdent < 1 && !partnere.includes(barnIdent)
							}
						)
						.required(messages.required)
				})
			)
		})
	})
})

function antallIdenter(values, path, identNr, type) {
	const index = path.charAt(path.indexOf('[') + 1)
	return _get(values.tpsf.relasjoner, type).reduce((acc, barn, idx) => {
		if (barn.ident === identNr && idx < index) {
			acc++
		}
		return acc
	}, 0)
}
