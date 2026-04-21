import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions, getInntektsaarOptions } from '@/pages/tenorSoek/utils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const SummertSkattegrunnlag = ({ handleChange }: any) => {
	const { domain: stadieOptions, loading: loadingStadie } = useTenorDomain('Stadietype')
	const { domain: typeOppgjoerOptions, loading: loadingTypeOppgjoer } =
		useTenorDomain('Oppgjoerstype')
	const { domain: tekniskNavnOptions, loading: loadingTekniskNavn } = useTenorDomain('TekniskNavn')

	const inntektsaarOptions = getInntektsaarOptions(6)

	return (
		<SoekKategori>
			<FormSelect
				name="summertSkattegrunnlag.inntektsaar"
				options={inntektsaarOptions}
				label="Inntektsår"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'summertSkattegrunnlag.inntektsaar',
						`Summert skattegrunnlag inntektsår: ${val?.label}`,
					)
				}
			/>
			<FormSelect
				name="summertSkattegrunnlag.stadietype"
				options={createOptions(stadieOptions?.data)}
				label="Stadie"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'summertSkattegrunnlag.stadietype',
						`Summert skattegrunnlag stadie: ${val?.label}`,
					)
				}
				isLoading={loadingStadie}
			/>
			<FormSelect
				name="summertSkattegrunnlag.oppgjoerstype"
				options={createOptions(typeOppgjoerOptions?.data)}
				label="Type oppgjør"
				size="large"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'summertSkattegrunnlag.oppgjoerstype',
						`Summert skattegrunnlag type oppgjør: ${val?.label}`,
					)
				}
				isLoading={loadingTypeOppgjoer}
			/>
			<FormSelect
				name="summertSkattegrunnlag.tekniskNavn"
				options={createOptions(tekniskNavnOptions?.data)}
				label="Teknisk navn"
				size="xxlarge"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'summertSkattegrunnlag.tekniskNavn',
						`Summert skattegrunnlag teknisk navn: ${val?.label}`,
					)
				}
				isLoading={loadingTekniskNavn}
			/>
			<div className="flexbox--flex-wrap">
				<FormTextInput
					name="summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.fraOgMed"
					label="Inntekt før fradrag f.o.m."
					type="number"
					onBlur={(val: any) =>
						handleChange(
							val?.target?.value || null,
							'summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.fraOgMed',
							`Summert skattegrunnlag inntekt før fradrag f.o.m.: ${val?.target?.value}`,
						)
					}
					visHvisAvhuket={false}
				/>
				<FormTextInput
					name="summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.tilOgMed"
					label="Inntekt før fradrag t.o.m."
					type="number"
					onBlur={(val: any) =>
						handleChange(
							val?.target?.value || null,
							'summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.tilOgMed',
							`Summert skattegrunnlag inntekt før fradrag t.o.m.: ${val?.target?.value}`,
						)
					}
					visHvisAvhuket={false}
				/>
			</div>
		</SoekKategori>
	)
}
