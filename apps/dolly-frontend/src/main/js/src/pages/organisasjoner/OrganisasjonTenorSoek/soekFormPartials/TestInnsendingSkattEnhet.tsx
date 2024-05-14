import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React from 'react'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'

export const TestInnsendingSkattEnhet = ({ handleChange, handleChangeList }: any) => {
	return (
		<SoekKategori>
			<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
				<FormSelect
					name="inntektsaar"
					options={[
						{ value: '2020', label: '2020' },
						{ value: '2021', label: '2021' },
						{ value: '2022', label: '2022' },
						{ value: '2023', label: '2023' },
					]}
					label="Organisasjonsform"
					onChange={(val: any) => handleChange(val?.value || null, 'inntektsaar')}
				/>
			</div>
			<FormCheckbox
				name="harSkattemeldingUtkast"
				label="Har skattemelding utkast"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harSkattemeldingUtkast')
				}
			/>
			<FormCheckbox
				name="harSkattemeldingFastsatt"
				label="Har skattemelding fastsatt"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harSkattemeldingFastsatt')
				}
			/>
			<FormCheckbox
				name="harSelskapsmeldingUtkast"
				label="Har selskapsmelding utkast"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harSelskapsmeldingUtkast')
				}
			/>
			<FormCheckbox
				name="harSelskapsmeldingFastsatt"
				label="Har selskapsmelding fastsatt"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harSelskapsmeldingFastsatt')
				}
			/>
			<FormCheckbox
				name="manglendeGrunnlagsdata"
				label="Manglende grunnlagsdata"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'manglendeGrunnlagsdata')
				}
			/>
			<FormCheckbox
				name="manntall"
				label="Manntall"
				onChange={(val: any) => handleChange(val?.target?.checked || undefined, 'manntall')}
			/>
		</SoekKategori>
	)
}
