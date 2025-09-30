import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React, { SyntheticEvent } from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { formatDate } from '@/utils/DataFormatter'

export const Arbeidsforhold = ({ handleChange }: any) => {
	const { domain: arbeidsforholdstypeOptions, loading: loadingArbeidsforholdstype } =
		useTenorDomain('Arbeidsforholdstype')

	return (
		<SoekKategori>
			<FormSelect
				name="arbeidsforhold.arbeidsforholdstype"
				options={createOptions(arbeidsforholdstypeOptions?.data)}
				size="xlarge"
				label="Arbeidsforholdstype"
				onChange={(val: SyntheticEvent) =>
					handleChange(
						val?.value || null,
						'arbeidsforhold.arbeidsforholdstype',
						`Arbeidsforholdstype: ${val?.label}`,
					)
				}
				isLoading={loadingArbeidsforholdstype}
			/>
			<div className="flexbox--flex-wrap">
				<FormDatepicker
					name="arbeidsforhold.startDatoPeriode.fraOgMed"
					label="Startdato f.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(
							val || null,
							'arbeidsforhold.startDatoPeriode.fraOgMed',
							`Arbeidsforhold startdato f.o.m.: ${formatDate(val)}`,
						)
					}
					visHvisAvhuket={false}
				/>
				<FormDatepicker
					name="arbeidsforhold.startDatoPeriode.tilOgMed"
					label="Startdato t.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(
							val || null,
							'arbeidsforhold.startDatoPeriode.tilOgMed',
							`Arbeidsforhold startdato t.o.m.: ${formatDate(val)}`,
						)
					}
					visHvisAvhuket={false}
				/>
				<FormDatepicker
					name="arbeidsforhold.sluttDatoPeriode.fraOgMed"
					label="Sluttdato f.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(
							val || null,
							'arbeidsforhold.sluttDatoPeriode.fraOgMed',
							`Arbeidsforhold sluttdato f.o.m.: ${formatDate(val)}`,
						)
					}
					visHvisAvhuket={false}
				/>
				<FormDatepicker
					name="arbeidsforhold.sluttDatoPeriode.tilOgMed"
					label="Sluttdato t.o.m."
					onChange={(val: SyntheticEvent) =>
						handleChange(
							val || null,
							'arbeidsforhold.sluttDatoPeriode.tilOgMed',
							`Arbeidsforhold sluttdato t.o.m.: ${formatDate(val)}`,
						)
					}
					visHvisAvhuket={false}
				/>
			</div>
			<FormCheckbox
				name="arbeidsforhold.harPermisjoner"
				label="Har permisjoner"
				onChange={(val: any) =>
					handleChange(
						val?.target?.checked || undefined,
						'arbeidsforhold.harPermisjoner',
						'Arbeidsforhold har permisjoner',
					)
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harPermitteringer"
				label="Har permitteringer"
				onChange={(val: any) =>
					handleChange(
						val?.target?.checked || undefined,
						'arbeidsforhold.harPermitteringer',
						'Arbeidsforhold har permitteringer',
					)
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harTimerMedTimeloenn"
				label="Har timer med timelønn"
				onChange={(val: any) =>
					handleChange(
						val?.target?.checked || undefined,
						'arbeidsforhold.harTimerMedTimeloenn',
						'Arbeidsforhold har timer med timelønn',
					)
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harUtenlandsopphold"
				label="Har utenlandsopphold"
				onChange={(val: any) =>
					handleChange(
						val?.target?.checked || undefined,
						'arbeidsforhold.harUtenlandsopphold',
						'Arbeidsforhold har utenlandsopphold',
					)
				}
			/>
			<FormCheckbox
				name="arbeidsforhold.harHistorikk"
				label="Har historikk"
				onChange={(val: any) =>
					handleChange(
						val?.target?.checked || undefined,
						'arbeidsforhold.harHistorikk',
						'Arbeidsforhold har historikk',
					)
				}
			/>
		</SoekKategori>
	)
}
