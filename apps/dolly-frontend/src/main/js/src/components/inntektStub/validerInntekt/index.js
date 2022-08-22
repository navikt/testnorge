import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import Inntekt from './Inntekt'
import { Formik } from 'formik'
import { useBoolean } from 'react-use'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import _ from 'lodash'

const InntektStub = ({ formikBag, inntektPath }) => {
	const [fields, setFields] = useState({})
	const [reset, setReset] = useBoolean(false)
	const [inntektValues] = useState(_get(formikBag.values, inntektPath))
	const [currentInntektstype, setCurrentInntektstype] = useState(
		_get(formikBag.values, `${inntektPath}.inntektstype`)
	)

	const tilleggsinformasjonAttributter = {
		BilOgBaat: 'bilOgBaat',
		DagmammaIEgenBolig: 'dagmammaIEgenBolig',
		NorskKontinentalsokkel: 'inntektPaaNorskKontinentalsokkel',
		Livrente: 'livrente',
		LottOgPartInnenFiske: 'lottOgPart',
		Nettoloennsordning: 'nettoloenn',
		UtenlandskArtist: 'utenlandskArtist',
	}

	useEffect(() => {
		setCurrentInntektstype(_get(formikBag.values, `${inntektPath}.inntektstype`))
	}, [formikBag.values])

	useEffect(() => {
		if (
			inntektValues.inntektstype &&
			inntektValues.inntektstype !== '' &&
			Object.keys(fields).length < 1
		) {
			InntektstubService.validate(_.omitBy(inntektValues, (value) => value === '' || !value)).then(
				(response) => {
					setFields(response)
				}
			)
		}
	}, [inntektValues, fields])

	const setFormikBag = (values) => {
		const nullstiltInntekt = {
			beloep: _get(formikBag.values, `${inntektPath}.beloep`),
			startOpptjeningsperiode: _get(formikBag.values, `${inntektPath}.startOpptjeningsperiode`),
			sluttOpptjeningsperiode: _get(formikBag.values, `${inntektPath}.sluttOpptjeningsperiode`),
			inntektstype: values.inntektstype,
		}

		if (values.inntektstype !== currentInntektstype) {
			formikBag.setFieldValue(inntektPath, nullstiltInntekt)
		} else {
			formikBag.setFieldValue(inntektPath, { ..._get(formikBag.values, inntektPath), ...values })
		}
	}

	useEffect(() => {
		Object.entries(fields).forEach((entry) => {
			const name = entry[0]
			const valueArray = entry[1]
			if (
				valueArray.length === 1 &&
				valueArray[0] === '<TOM>' &&
				_get(formikBag.values, `${inntektPath}.${name}`)
			) {
				formikBag.setFieldValue(`${inntektPath}.${name}`, undefined)
			}
		})
	}, [fields])

	return (
		<Formik
			initialValues={inntektValues.inntektstype !== '' ? inntektValues : {}}
			onSubmit={(values, { resetForm }) => {
				if (currentInntektstype && values.inntektstype !== currentInntektstype) {
					resetForm({ values: { inntektstype: values.inntektstype } })
					values = { inntektstype: values.inntektstype }
					setReset(true)
				} else {
					setReset(false)
				}
				const emptyableFields = Object.entries(fields).filter(
					(field) => field?.[1]?.[0] === '<TOM>' && field?.[1]?.length > 2
				)
				for (const [key] of emptyableFields) {
					if (!values[key] && key !== 'tilleggsinformasjonstype') {
						values[key] = '<TOM>'
					}
				}
				InntektstubService.validate(_.omitBy(values, (value) => value === '' || !value)).then(
					(response) => setFields(response)
				)
				for (const [key, value] of Object.entries(fields)) {
					if (values[key] === undefined && value.length !== 1) {
						values[key] = null
					}
				}

				for (const [key, value] of Object.entries(values)) {
					if (value === '' || value === '<TOM>') {
						values[key] = undefined
					}
				}
				setFormikBag(values)
			}}
			component={({ handleSubmit }) => (
				<div>
					<Inntekt
						fields={fields}
						onValidate={handleSubmit}
						formikBag={formikBag}
						path={inntektPath}
						resetForm={reset}
						tilleggsinformasjonAttributter={tilleggsinformasjonAttributter}
					/>
				</div>
			)}
		/>
	)
}

export default InntektStub
