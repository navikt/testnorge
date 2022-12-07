import React, { useEffect, useState } from 'react'
import Inntekt from './inntekt'
import { Formik } from 'formik'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import * as _ from 'lodash-es'

const tilleggsinformasjonAttributter = {
	BilOgBaat: 'bilOgBaat',
	DagmammaIEgenBolig: 'dagmammaIEgenBolig',
	NorskKontinentalsokkel: 'inntektPaaNorskKontinentalsokkel',
	Livrente: 'livrente',
	LottOgPartInnenFiske: 'lottOgPart',
	Nettoloennsordning: 'nettoloenn',
	UtenlandskArtist: 'utenlandskArtist',
}

const InntektStub = ({ formikBag, inntektPath }) => {
	const [fields, setFields] = useState({})
	const [inntektValues] = useState(_.get(formikBag.values, inntektPath))
	const [currentInntektstype, setCurrentInntektstype] = useState(
		_.get(formikBag.values, `${inntektPath}.inntektstype`)
	)
	const currentTilleggsinformasjonstype = _.get(
		formikBag.values,
		`${inntektPath}.tilleggsinformasjonstype`
	)

	useEffect(() => {
		setCurrentInntektstype(_.get(formikBag.values, `${inntektPath}.inntektstype`))
	}, [formikBag.values])

	useEffect(() => {
		if (
			inntektValues.inntektstype &&
			inntektValues.inntektstype !== '' &&
			Object.keys(fields).length < 1
		) {
			InntektstubService.validate(_.omitBy(inntektValues, (value) => value === '' || !value)).then(
				(response) => setFields(response)
			)
		}
	}, [inntektValues, fields])

	useEffect(() => {
		Object.entries(fields).forEach((entry) => {
			removeEmptyFieldsFromFormik(entry)
		})
	}, [fields])

	useEffect(() => {
		if (!currentTilleggsinformasjonstype) {
			formikBag.setFieldValue(`${inntektPath}.tilleggsinformasjon`, undefined)
			return
		}
		formikBag.setFieldValue(`${inntektPath}.tilleggsinformasjon`, {
			[`${tilleggsinformasjonAttributter[currentTilleggsinformasjonstype]}`]: {},
		})
	}, [currentTilleggsinformasjonstype])

	const setFormikBag = (values) => {
		const nullstiltInntekt = {
			beloep: _.get(formikBag.values, `${inntektPath}.beloep`),
			startOpptjeningsperiode: _.get(formikBag.values, `${inntektPath}.startOpptjeningsperiode`),
			sluttOpptjeningsperiode: _.get(formikBag.values, `${inntektPath}.sluttOpptjeningsperiode`),
			inntektstype: values.inntektstype,
		}

		if (values.inntektstype !== currentInntektstype) {
			formikBag.setFieldValue(inntektPath, nullstiltInntekt)
		} else {
			formikBag.setFieldValue(inntektPath, { ..._.get(formikBag.values, inntektPath), ...values })
		}
	}

	const removeEmptyFieldsFromFormik = (entry) => {
		const name = entry[0]
		const valueArray = entry[1]
		if (
			valueArray.length === 1 &&
			valueArray[0] === '<TOM>' &&
			_.get(formikBag.values, `${inntektPath}.${name}`)
		) {
			formikBag.setFieldValue(`${inntektPath}.${name}`, undefined)
		}
	}

	const clearEmptyValuesAndFields = (values) => {
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
	}

	return (
		<Formik
			initialValues={inntektValues.inntektstype !== '' ? inntektValues : {}}
			onSubmit={(values) => {
				if (currentInntektstype && values.inntektstype !== currentInntektstype) {
					values = { inntektstype: values.inntektstype }
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
				clearEmptyValuesAndFields(values)
				setFormikBag(values)
			}}
			component={({ handleSubmit }) => (
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
