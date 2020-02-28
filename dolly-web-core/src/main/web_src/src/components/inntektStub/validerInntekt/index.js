import React, { useState } from 'react'
import _get from 'lodash/get'
import Inntekt from './inntekt'
import { Formik } from 'formik'
import * as api from '../api'
import tilleggsinformasjonPaths from '../paths'

const InntektStub = ({ formikBag, inntektPath }) => {
	const [fields, setFields] = useState({})
	// const [inntektstype, setInntektstype] = useState(
	// 	_get(formikBag.values, `${inntektPath}.inntektstype`)
	// )

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

	return (
		<Formik
			initialValues={{}}
			onSubmit={
				values =>
					api
						.validate(values)
						.then(response => setFields(response))
						.then(() => setFormikBag(values))
				// .then(() => setInntektstype(values.inntektstype))
			}
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
