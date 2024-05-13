import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React, { SyntheticEvent } from 'react'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const EnhetsregisteretArbeidsforhold = ({ handleChange }: any) => (
	<SoekKategori>
		<FormDatepicker
			name="tenorRelasjoner.arbeidsforhold.startDato.fraOgMed"
			label="Start Dato f.o.m"
			onChange={(val: SyntheticEvent) =>
				handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.startDato.fraOgMed')
			}
			visHvisAvhuket={false}
		/>
		<FormDatepicker
			name="tenorRelasjoner.arbeidsforhold.startDato.tilOgMed"
			label="Start Dato t.o.m"
			onChange={(val: SyntheticEvent) =>
				handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.startDato.tilOgMed')
			}
			visHvisAvhuket={false}
		/>
		<FormDatepicker
			name="tenorRelasjoner.arbeidsforhold.slutDato.fraOgMed"
			label="Slutt Dato f.o.m"
			onChange={(val: SyntheticEvent) =>
				handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.sluttDato.fraOgMed')
			}
			visHvisAvhuket={false}
		/>
		<FormDatepicker
			name="tenorRelasjoner.arbeidsforhold.sluttDato.tilOgMed"
			label="Slutt Dato t.o.m"
			onChange={(val: SyntheticEvent) =>
				handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.sluttDato.tilOgMed')
			}
			visHvisAvhuket={false}
		/>
		<FormTextInput
			name="tenorRelasjoner.arbeidsforhold.arbeidsforholdtype"
			label="Arbeidsforhold Type"
			onBlur={(val: any) =>
				handleChange(
					val?.target?.value || null,
					'tenorRelasjoner.arbeidsforhold.arbeidsforholdtype',
				)
			}
		/>
		<FormCheckbox
			name="tenorRelasjoner.arbeidsforhold.harPermisjoner"
			label="Har Permisjoner"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.arbeidsforhold.harPermisjoner',
				)
			}
		/>
		<FormCheckbox
			name="tenorRelasjoner.arbeidsforhold.harPermitteringer"
			label="Har Permitteringer"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.arbeidsforhold.harPermitteringer',
				)
			}
		/>
		<FormCheckbox
			name="tenorRelasjoner.arbeidsforhold.harTimerMedTimeloenn"
			label="Har Timer Med Timeloenn"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.arbeidsforhold.harTimerMedTimeloenn',
				)
			}
		/>
		<FormCheckbox
			name="tenorRelasjoner.arbeidsforhold.harUtenlandsopphold"
			label="Har Utenlandsopphold"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.arbeidsforhold.harUtenlandsopphold',
				)
			}
		/>
		<FormCheckbox
			name="tenorRelasjoner.arbeidsforhold.harHistorikk"
			label="Har Historikk"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.arbeidsforhold.harHistorikk',
				)
			}
		/>
	</SoekKategori>
)
