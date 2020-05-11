import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubOrgnummerSelect } from './inntektstubOrgnummerSelect'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { VersjonInfo } from './inntektinformasjonTypes'

interface InntektsinformasjonForm {
	path: string
	formikBag: FormikProps<{}>
	locked: boolean
	versjonInfo: VersjonInfo
}

const lockedHoverText = 'Historikk må ha samme virksomhet og år/måned som gjeldende inntekt'

export default ({ path, formikBag, locked, versjonInfo }: InntektsinformasjonForm) => {
	const handleChange = (inputPath: string, value: string, label: string) => {
		formikBag.setFieldValue(`${inputPath}.${label}`, value)

		if (versjonInfo.gjeldendeInntektMedHistorikk) {
			versjonInfo.underversjoner.forEach(versjon => {
				formikBag.setFieldValue(`${versjonInfo.path}[${versjon}].${label}`, value)
			})
		}
	}

	return (
		<div key={path} title={locked ? lockedHoverText : undefined}>
			<div className="flexbox--flex-wrap">
				<DollyTextInput
					name="sisteAarMaaned"
					label="Måned/år"
					type="month"
					value={_get(formikBag.values, `${path}.sisteAarMaaned`)}
					onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
						handleChange(path, e.target.value, e.target.name)
					}
					disabled={locked}
				/>
				<DollyTextInput
					name="antallMaaneder"
					label="Generer antall måneder"
					type="number"
					value={_get(formikBag.values, `${path}.antallMaaneder`)}
					onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
						handleChange(path, e.target.value, e.target.name)
					}
					disabled={locked}
				/>
				<InntektstubOrgnummerSelect
					path={path}
					locked={locked}
					formikBag={formikBag}
					versjonInfo={versjonInfo}
				/>
			</div>
			<InntektForm formikBag={formikBag} inntektsinformasjonPath={path} />
			<FradragForm formikBag={formikBag} inntektsinformasjonPath={path} />
			<ForskuddstrekkForm formikBag={formikBag} inntektsinformasjonPath={path} />
			<ArbeidsforholdForm formikBag={formikBag} inntektsinformasjonPath={path} />
		</div>
	)
}
