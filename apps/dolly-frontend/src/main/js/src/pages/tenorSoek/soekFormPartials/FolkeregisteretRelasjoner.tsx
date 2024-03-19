import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React, { SyntheticEvent } from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const FolkeregisteretRelasjoner = ({ handleChange }: any) => {
	const { domain: relasjonOptions } = useTenorDomain('Relasjon')

	return (
		<SoekKategori>
			<FormSelect
				name="relasjoner.relasjon"
				options={createOptions(relasjonOptions?.data)}
				label="Relasjon"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'relasjoner.relasjon')}
			/>
			<div className="flexbox--flex-wrap">
				<FormTextInput
					name="relasjoner.antallBarn.fraOgMed"
					label="Antall barn f.o.m."
					type="number"
					onBlur={(val: SyntheticEvent) =>
						handleChange(val?.target?.value || null, 'relasjoner.antallBarn.fraOgMed')
					}
					visHvisAvhuket={false}
				/>
				<FormTextInput
					name="relasjoner.antallBarn.tilOgMed"
					label="Antall barn t.o.m."
					type="number"
					onBlur={(val: SyntheticEvent) =>
						handleChange(val?.target?.value || null, 'relasjoner.antallBarn.tilOgMed')
					}
					visHvisAvhuket={false}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormTextInput
					name="relasjoner.relasjonMedFoedselsaar.fraOgMed"
					label="Relasjon med fødselsår f.o.m."
					type="number"
					onBlur={(val: SyntheticEvent) =>
						handleChange(val?.target?.value || null, 'relasjoner.relasjonMedFoedselsaar.fraOgMed')
					}
					visHvisAvhuket={false}
				/>
				<FormTextInput
					name="relasjoner.relasjonMedFoedselsaar.tilOgMed"
					label="Relasjon med fødselsår t.o.m."
					type="number"
					onBlur={(val: SyntheticEvent) =>
						handleChange(val?.target?.value || null, 'relasjoner.relasjonMedFoedselsaar.tilOgMed')
					}
					visHvisAvhuket={false}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormCheckbox
					name="relasjoner.harForeldreAnsvar"
					label="Har foreldreansvar"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.harForeldreAnsvar')
					}
				/>
				<FormCheckbox
					name="relasjoner.harDeltBosted"
					label="Har delt bosted"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.harDeltBosted')
					}
				/>
				<FormCheckbox
					name="relasjoner.harVergemaalEllerFremtidsfullmakt"
					label="Har vergemål eller fremtidsfullmakt"
					onChange={(val: SyntheticEvent) =>
						handleChange(
							val?.target?.checked || undefined,
							'relasjoner.harVergemaalEllerFremtidsfullmakt',
						)
					}
				/>
				<FormCheckbox
					name="relasjoner.borMedMor"
					label="Bor med mor"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.borMedMor')
					}
				/>
				<FormCheckbox
					name="relasjoner.borMedFar"
					label="Bor med far"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.borMedFar')
					}
				/>
				<FormCheckbox
					name="relasjoner.borMedMedmor"
					label="Bor med medmor"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.borMedMedmor')
					}
				/>
				<FormCheckbox
					name="relasjoner.foreldreHarSammeAdresse"
					label="Foleldre har samme adresse"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.foreldreHarSammeAdresse')
					}
				/>
			</div>
		</SoekKategori>
	)
}
