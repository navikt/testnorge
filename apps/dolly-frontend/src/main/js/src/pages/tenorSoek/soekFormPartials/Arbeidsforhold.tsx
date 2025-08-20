import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React, { SyntheticEvent } from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const Arbeidsforhold = ({ handleChange, handleChangeList, getValue }: any) => {
	const { domain: arbeidsforholdstypeOptions } = useTenorDomain('Arbeidsforholdstype')

	return (
		<SoekKategori>
			<FormSelect
				name="arbeidsforhold.arbeidsforholdstype"
				options={createOptions(arbeidsforholdstypeOptions?.data)}
				size="xlarge"
				label="Arbeidsforholdstype"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.value || null, 'arbeidsforhold.arbeidsforholdstype')
				}
			/>
			<div className="flexbox--flex-wrap">
				<FormDatepicker
					name="arbeidsforhold.startDatoPeriode.fraOgMed"
					label="Startdato f.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(val || null, 'arbeidsforhold.startDatoPeriode.fraOgMed')
					}
					visHvisAvhuket={false}
				/>
				<FormDatepicker
					name="arbeidsforhold.startDatoPeriode.tilOgMed"
					label="Startdato t.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(val || null, 'arbeidsforhold.startDatoPeriode.tilOgMed')
					}
					visHvisAvhuket={false}
				/>
				<FormDatepicker
					name="arbeidsforhold.sluttDatoPeriode.fraOgMed"
					label="Sluttdato f.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(val || null, 'arbeidsforhold.sluttDatoPeriode.fraOgMed')
					}
					visHvisAvhuket={false}
				/>
				<FormDatepicker
					name="arbeidsforhold.sluttDatoPeriode.tilOgMed"
					label="Sluttdato t.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(val || null, 'arbeidsforhold.sluttDatoPeriode.tilOgMed')
					}
					visHvisAvhuket={false}
				/>
			</div>
			<FormCheckbox
				name="arbeidsforhold.harPermisjoner"
				label="Har permisjoner"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'arbeidsforhold.harPermisjoner')
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harPermitteringer"
				label="Har permitteringer"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'arbeidsforhold.harPermitteringer')
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harTimerMedTimeloenn"
				label="Har timer med timelønn"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'arbeidsforhold.harTimerMedTimeloenn')
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harUtenlandsopphold"
				label="Har utenlandsopphold"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'arbeidsforhold.harUtenlandsopphold')
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harHistorikk"
				label="Har historikk"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'arbeidsforhold.harHistorikk')
				}
			/>
		</SoekKategori>
	)
}
