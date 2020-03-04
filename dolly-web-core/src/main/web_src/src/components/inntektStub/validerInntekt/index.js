import React, { useState, useEffect } from 'react'
import _get from 'lodash/get'
import Inntekt from './inntekt'
import { Formik } from 'formik'
import * as api from '../api'
import tilleggsinformasjonPaths from '../paths'

const InntektStub = ({ formikBag, inntektPath }) => {
	const [fields, setFields] = useState({})
	// TODO: kanskje bruke formikBag.values for å hente fields når man går fram og tilbake?
	const [currentInntektstype, setCurrentInntektstype] = useState(
		_get(formikBag.values, `${inntektPath}.inntektstype`)
	)

	useEffect(() => {
		setCurrentInntektstype(_get(formikBag.values, `${inntektPath}.inntektstype`))
		// console.log('currentInntektstype :', currentInntektstype)
	})

	const setFormikBag = values => {
		const tilleggsinformasjonAttributter = {
			BilOgBaat: 'bilOgBaat', // Lønnsinntekt
			DagmammaIEgenBolig: 'dagmammaIEgenBolig', // Næringsinntekt
			NorskKontinentalsokkel: 'inntektPaaNorskKontinentalsokkel', // Lønnsinntekt
			Livrente: 'livrente', // Pensjon eller trygd
			LottOgPartInnenFiske: 'lottOgPart', // Næringsinntekt
			Nettoloennsordning: 'nettoloenn', // Lønnsinntekt
			UtenlandskArtist: 'utenlandskArtist' // Lønnsinntekt
		}

		const nullstiltInntekt = {
			beloep: _get(formikBag.values, `${inntektPath}.beloep`),
			startOpptjeningsperiode: _get(formikBag.values, `${inntektPath}.startOpptjeningsperiode`),
			sluttOpptjeningsperiode: _get(formikBag.values, `${inntektPath}.sluttOpptjeningsperiode`),
			inntektstype: values.inntektstype
		}

		if (values.inntektstype !== currentInntektstype) {
			formikBag.setFieldValue(inntektPath, nullstiltInntekt)
		} else {
			for (var [key, value] of Object.entries(values)) {
				tilleggsinformasjonAttributter[value]
					? formikBag.setFieldValue(
							`${inntektPath}.tilleggsinformasjon.${tilleggsinformasjonAttributter[value]}`,
							{}
					  )
					: tilleggsinformasjonPaths(key) !== key
					? formikBag.setFieldValue(`${inntektPath}.${tilleggsinformasjonPaths(key)}`, value)
					: formikBag.setFieldValue(`${inntektPath}.${key}`, value)
			}
		}
	}

	return (
		<Formik
			initialValues={{}}
			onSubmit={(values, { resetForm }) => {
				if (currentInntektstype && values.inntektstype !== currentInntektstype) {
					resetForm({ values: { inntektstype: values.inntektstype } })
					values = { inntektstype: values.inntektstype }
				}
				api.validate(values).then(response => setFields(response))
				setFormikBag(values)
			}}
			render={({ handleSubmit }) => (
				<div>
					<Inntekt
						fields={fields}
						onValidate={handleSubmit}
						formikBag={formikBag}
						path={inntektPath}
					/>
				</div>
			)}
		/>
	)
}

export default InntektStub
