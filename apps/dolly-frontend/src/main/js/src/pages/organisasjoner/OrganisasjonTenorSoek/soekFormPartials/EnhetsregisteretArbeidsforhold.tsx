import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import React, { SyntheticEvent } from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Option } from '@/service/SelectOptionsOppslag'
import { useTenorOrganisasjonDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'

export const EnhetsregisteretArbeidsforhold = ({ handleChange }: any) => {
	const { domain: arbeidsforholdTypeOptions } = useTenorOrganisasjonDomain('ArbeidsforholdType')
	return (
		<SoekKategori>
			<FormDatepicker
				name="tenorRelasjoner.arbeidsforhold.startDato.fraOgMed"
				label="Ansettelse start f.o.m"
				onChange={(val: SyntheticEvent) =>
					handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.startDato.fraOgMed')
				}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="tenorRelasjoner.arbeidsforhold.startDato.tilOgMed"
				label="Ansettelse start t.o.m"
				onChange={(val: SyntheticEvent) =>
					handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.startDato.tilOgMed')
				}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="tenorRelasjoner.arbeidsforhold.sluttDato.fraOgMed"
				label="Ansettelse slutt f.o.m"
				onChange={(val: SyntheticEvent) =>
					handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.sluttDato.fraOgMed')
				}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="tenorRelasjoner.arbeidsforhold.sluttDato.tilOgMed"
				label="Ansettelse slutt t.o.m"
				onChange={(val: SyntheticEvent) =>
					handleChange(val || null, 'tenorRelasjoner.arbeidsforhold.sluttDato.tilOgMed')
				}
				visHvisAvhuket={false}
			/>
			<div className={'flexbox'} style={{ flexFlow: 'wrap' }}>
				<FormSelect
					name="tenorRelasjoner.arbeidsforhold.harPermisjoner"
					label="Har permisjoner"
					options={Options('boolean')}
					onChange={(val: Option) =>
						handleChange(val?.value, 'tenorRelasjoner.arbeidsforhold.harPermisjoner')
					}
				/>
				<FormSelect
					name="tenorRelasjoner.arbeidsforhold.harPermitteringer"
					label="Har permitteringer"
					options={Options('boolean')}
					onChange={(val: Option) =>
						handleChange(val?.value, 'tenorRelasjoner.arbeidsforhold.harPermitteringer')
					}
				/>
				<FormSelect
					name="tenorRelasjoner.arbeidsforhold.harTimerMedTimeloenn"
					label="Har timer med timeloenn"
					options={Options('boolean')}
					onChange={(val: Option) =>
						handleChange(val?.value, 'tenorRelasjoner.arbeidsforhold.harTimerMedTimeloenn')
					}
				/>
				<FormSelect
					name="tenorRelasjoner.arbeidsforhold.harUtenlandsopphold"
					label="Har utenlandsopphold"
					options={Options('boolean')}
					onChange={(val: Option) =>
						handleChange(val?.value, 'tenorRelasjoner.arbeidsforhold.harUtenlandsopphold')
					}
				/>
				<FormSelect
					name="tenorRelasjoner.arbeidsforhold.harHistorikk"
					label="Har historikk"
					options={Options('boolean')}
					onChange={(val: Option) =>
						handleChange(val?.value, 'tenorRelasjoner.arbeidsforhold.harHistorikk')
					}
				/>
				<FormSelect
					name="tenorRelasjoner.arbeidsforhold.arbeidsforholdtype"
					label="Arbeidsforholdstype"
					options={createOptions(arbeidsforholdTypeOptions?.data)}
					onChange={(val: Option) =>
						handleChange(val?.value, 'tenorRelasjoner.arbeidsforhold.arbeidsforholdtype')
					}
				/>
			</div>
		</SoekKategori>
	)
}
