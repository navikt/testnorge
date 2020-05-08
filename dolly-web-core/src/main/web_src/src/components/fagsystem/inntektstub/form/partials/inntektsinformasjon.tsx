import React from 'react'
import _get from 'lodash/get'
import { FormikProps, FieldArray } from 'formik'
import {
	DollyFieldArrayWrapper,
	DollyFaBlokk,
	FieldArrayAddButton
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import InntektsinformasjonForm from './inntektsinformasjonForm'
import { Inntektsinformasjon } from './inntektinformasjonTypes'
import Versjonsinformasjon from './versjon'

interface InntektsinformasjonInput {
	formikBag: FormikProps<{}>
}

export const initialValues: Inntektsinformasjon = {
	sisteAarMaaned: '',
	antallMaaneder: '',
	virksomhet: '',
	opplysningspliktig: '',
	versjon: null,
	inntektsliste: [
		{
			beloep: '',
			startOpptjeningsperiode: undefined,
			sluttOpptjeningsperiode: undefined,
			inntektstype: ''
		}
	],
	fradragsliste: [],
	forskuddstrekksliste: [],
	arbeidsforholdsliste: []
}

const addNyVersjonEntry = (
	formikBag: FormikProps<{}>,
	inntektValues: Array<Inntektsinformasjon>,
	path: string,
	idx: number
) => {
	const versjonBasertPaa = _get(formikBag.values, path)
	const newEntryIdx = idx + 1

	const nyInntektValues = [...inntektValues]
	nyInntektValues.splice(newEntryIdx, 0, {
		...versjonBasertPaa,
		versjon: versjonBasertPaa.versjon === null ? 1 : versjonBasertPaa.versjon + 1
	})
	formikBag.setFieldValue(inntektstubPath, nyInntektValues)
}

const sjekkKanOppretteNyVersjon = (values: Inntektsinformasjon): boolean =>
	values.virksomhet !== '' && values.sisteAarMaaned !== ''

const inntektstubPath = 'inntektstub.inntektsinformasjon'
const infotekst: string =
	'For å generere samme inntektsinformasjon for flere måneder - fyll inn siste måned/år, samt antall måneder bakover inntektsinformasjonen skal genereres for.'

export default ({ formikBag }: InntektsinformasjonInput) => (
	<div className="flexbox--flex-wrap">
		<FieldArray name={inntektstubPath}>
			{arrayHelpers => {
				const inntektValues = _get(arrayHelpers.form.values, inntektstubPath, [])
				return (
					<DollyFieldArrayWrapper>
						{inntektValues.map((curr: Inntektsinformasjon, idx: number) => {
							const path: string = `${inntektstubPath}.${idx}`
							const locked: boolean = curr.versjon !== null

							const {
								gjeldendeInntektMedHistorikk,
								underversjonerIdx,
								gjeldendeIdx
							} = Versjonsinformasjon(formikBag, inntektstubPath, inntektValues, idx)

							const visSlett: boolean = !gjeldendeInntektMedHistorikk
							const handleRemove = () => arrayHelpers.remove(idx)

							const kanOppretteNyVersjon: boolean = sjekkKanOppretteNyVersjon(curr)
							const visOpprettNyVersjon: boolean =
								visSlett || curr.versjon === underversjonerIdx.length

							const visningIdx: string | number = curr.versjon
								? `Historikk (versjon ${curr.versjon})`
								: gjeldendeIdx
							const header: string = `Inntektsinformasjon ${
								gjeldendeInntektMedHistorikk ? '(Gjeldende)' : ''
							}`

							return (
								<DollyFaBlokk
									key={idx}
									idx={visningIdx}
									header={header}
									handleRemove={visSlett && handleRemove}
									hjelpetekst={infotekst}
								>
									<InntektsinformasjonForm
										path={path}
										locked={locked}
										formikBag={formikBag}
										versjonInfo={{
											underversjoner: underversjonerIdx,
											path: inntektstubPath,
											gjeldendeInntektMedHistorikk: gjeldendeInntektMedHistorikk
										}}
									/>
									{visOpprettNyVersjon && (
										<FieldArrayAddButton
											hoverText={!kanOppretteNyVersjon && 'Måned/år og virksomhet må være utfylt'}
											disabled={!kanOppretteNyVersjon}
											addEntryButtonText="Ny versjon av inntekt (historikk)"
											onClick={() => addNyVersjonEntry(formikBag, inntektValues, path, idx)}
										/>
									)}
								</DollyFaBlokk>
							)
						})}
						<FieldArrayAddButton
							addEntryButtonText="Inntektsinfo"
							onClick={() => arrayHelpers.push(initialValues)}
						/>
					</DollyFieldArrayWrapper>
				)
			}}
		</FieldArray>
	</div>
)
