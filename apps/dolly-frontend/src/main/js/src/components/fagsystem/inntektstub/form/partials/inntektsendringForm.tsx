import React from 'react'
import _get from 'lodash/get'
import { FieldArray, FormikProps } from 'formik'
import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Arbeidsforhold, Forskudd, Fradrag, Inntekt } from './inntektstubTypes'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface InntektendringForm {
	formikBag: FormikProps<{}>
	path: string
}

type Inntektslister = {
	inntektsliste: Array<Inntekt>
	fradragsliste: Array<Fradrag>
	forskuddstrekksliste: Array<Forskudd>
	arbeidsforholdsliste: Array<Arbeidsforhold>
}

const hjelpetekst = `Den øverste inntektinformasjonen er den gjeldende inntekten. All inntektsinformasjon merket med "Versjon #" er historiske endringer der økende versjonsnummer er nyere.`

export default ({ formikBag, path }: InntektendringForm) => {
	const kopiAvGjeldendeInntekt = _get(formikBag.values, path)
	const initialValues: Inntektslister = {
		arbeidsforholdsliste: kopiAvGjeldendeInntekt.arbeidsforholdsliste,
		forskuddstrekksliste: kopiAvGjeldendeInntekt.forskuddstrekksliste,
		fradragsliste: kopiAvGjeldendeInntekt.fradragsliste,
		inntektsliste: kopiAvGjeldendeInntekt.inntektsliste,
	}
	const historikkPath = `${path}.historikk`
	const data = _get(formikBag.values, historikkPath, [])

	return (
		<FieldArray name={historikkPath}>
			{(arrayHelpers) => {
				const addNewEntry = () => arrayHelpers.push(initialValues)
				return (
					<ErrorBoundary>
						<DollyFieldArrayWrapper>
							{data.map((c: Inntektslister, idx: number) => {
								const listePath = `${historikkPath}[${idx}]`
								const clickRemove = () => arrayHelpers.remove(idx)
								return (
									<DollyFaBlokk
										key={idx}
										idx={idx}
										hjelpetekst={hjelpetekst}
										header={`Inntektsendring (versjon ${idx + 1})`}
										handleRemove={clickRemove}
									>
										<InntektsinformasjonLister formikBag={formikBag} path={listePath} />
									</DollyFaBlokk>
								)
							})}
							<FieldArrayAddButton
								addEntryButtonText="Inntektsendring (historikk)"
								onClick={addNewEntry}
							/>
						</DollyFieldArrayWrapper>
					</ErrorBoundary>
				)
			}}
		</FieldArray>
	)
}
