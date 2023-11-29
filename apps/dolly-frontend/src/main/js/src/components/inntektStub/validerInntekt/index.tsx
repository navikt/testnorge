import React, { useEffect, useState } from 'react'
import Inntekt from '@/components/inntektStub/validerInntekt/Inntekt'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import * as _ from 'lodash'
import { Form, useFormContext } from 'react-hook-form'

const tilleggsinformasjonAttributter = {
	BilOgBaat: 'bilOgBaat',
	DagmammaIEgenBolig: 'dagmammaIEgenBolig',
	NorskKontinentalsokkel: 'inntektPaaNorskKontinentalsokkel',
	Livrente: 'livrente',
	LottOgPartInnenFiske: 'lottOgPart',
	Nettoloennsordning: 'nettoloenn',
	UtenlandskArtist: 'utenlandskArtist',
	BonusFraForsvaret: 'bonusFraForsvaret',
	ReiseKostOgLosji: 'reiseKostOgLosji',
}

const InntektStub = ({ inntektPath }) => {
	console.log('inntektPath: ', inntektPath) //TODO - SLETT MEG
	const formMethods = useFormContext()
	const [fields, setFields] = useState({})
	const inntektValues = formMethods.watch(inntektPath)
	const {
		beloep,
		startOpptjeningsperiode,
		sluttOpptjeningsperiode,
		inntektstype,
		tilleggsinformasjonstype,
	} = inntektValues

	useEffect(() => {
		console.log('inntektValues: ', inntektValues) //TODO - SLETT MEG
		if (inntektstype !== '' && Object.keys(fields).length < 1) {
			InntektstubService.validate(_.omitBy(inntektValues, (value) => value === '' || !value)).then(
				(response) => setFields(response),
			)
		}
	}, [inntektValues, fields])

	useEffect(() => {
		Object.entries(fields).forEach((entry) => {
			removeEmptyFieldsFromFormik(entry)
		})
	}, [fields])

	useEffect(() => {
		if (!tilleggsinformasjonstype) {
			formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, undefined)
			return
		}
		formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, {
			[`${tilleggsinformasjonAttributter[tilleggsinformasjonstype]}`]: {},
		})
	}, [tilleggsinformasjonstype])

	const setForm = (values) => {
		const nullstiltInntekt = {
			beloep: beloep,
			startOpptjeningsperiode: startOpptjeningsperiode,
			sluttOpptjeningsperiode: sluttOpptjeningsperiode,
			inntektstype: inntektstype,
		}

		if (values.inntektstype !== inntektstype) {
			formMethods.setValue(inntektPath, nullstiltInntekt)
		} else {
			formMethods.setValue(inntektPath, {
				...inntektValues,
				...values,
			})
		}
	}

	const removeEmptyFieldsFromFormik = (entry) => {
		const name = entry[0]
		const valueArray = entry[1]
		if (
			valueArray.length === 1 &&
			valueArray[0] === '<TOM>' &&
			_.get(formMethods.getValues(), `${inntektPath}.${name}`)
		) {
			formMethods.setValue(`${inntektPath}.${name}`, undefined)
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
		<Form
			onSubmit={(values) => {
				console.log('values, kapplah!: ', values) //TODO - SLETT MEG
				if (inntektstype && values.inntektstype !== inntektstype) {
					values = { inntektstype: values.inntektstype }
				}
				const emptyableFields = Object.entries(fields).filter(
					(field) => field?.[1]?.[0] === '<TOM>' && field?.[1]?.length > 2,
				)
				for (const [key] of emptyableFields) {
					if (!values[key] && key !== 'tilleggsinformasjonstype') {
						values[key] = '<TOM>'
					}
				}
				InntektstubService.validate(_.omitBy(values, (value) => value === '' || !value)).then(
					(response) => setFields(response),
				)
				clearEmptyValuesAndFields(values)
				setForm(values)
			}}
		>
			<div>
				<Inntekt
					fields={fields}
					onValidate={() => formMethods.trigger('inntekt')}
					formMethods={formMethods}
					path={inntektPath}
				/>
			</div>
		</Form>
	)
}

export default InntektStub
