import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const FolkeregisteretRelasjoner = ({ handleChange }: any) => {
	const { domain: relasjonOptions, loading: loadingRelasjon } = useTenorDomain('Relasjon')

	return (
		<SoekKategori>
			<FormSelect
				name="relasjoner.relasjon"
				options={createOptions(relasjonOptions?.data)}
				label="Relasjon"
				onChange={(val: any) =>
					handleChange(val?.value || null, 'relasjoner.relasjon', `Relasjon: ${val?.label}`)
				}
				isLoading={loadingRelasjon}
			/>
			<div className="flexbox--flex-wrap">
				<FormTextInput
					name="relasjoner.antallBarn.fraOgMed"
					label="Antall barn f.o.m."
					type="number"
					// @ts-ignore
					onBlur={(val: any) =>
						handleChange(
							val?.target?.value || null,
							'relasjoner.antallBarn.fraOgMed',
							`Antall barn f.o.m.: ${val?.target?.value}`,
						)
					}
					visHvisAvhuket={false}
				/>
				<FormTextInput
					name="relasjoner.antallBarn.tilOgMed"
					label="Antall barn t.o.m."
					type="number"
					// @ts-ignore
					onBlur={(val: any) =>
						handleChange(
							val?.target?.value || null,
							'relasjoner.antallBarn.tilOgMed',
							`Antall barn t.o.m.: ${val?.target?.value}`,
						)
					}
					visHvisAvhuket={false}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormTextInput
					name="relasjoner.relasjonMedFoedselsaar.fraOgMed"
					label="Relasjon med fødselsår f.o.m."
					type="number"
					// @ts-ignore
					onBlur={(val: any) =>
						handleChange(
							val?.target?.value || null,
							'relasjoner.relasjonMedFoedselsaar.fraOgMed',
							`Relasjon med fødselsår f.o.m.: ${val?.target?.value}`,
						)
					}
					visHvisAvhuket={false}
				/>
				<FormTextInput
					name="relasjoner.relasjonMedFoedselsaar.tilOgMed"
					label="Relasjon med fødselsår t.o.m."
					type="number"
					// @ts-ignore
					onBlur={(val: any) =>
						handleChange(
							val?.target?.value || null,
							'relasjoner.relasjonMedFoedselsaar.tilOgMed',
							`Relasjon med fødselsår t.o.m.: ${val?.target?.value}`,
						)
					}
					visHvisAvhuket={false}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormCheckbox
					name="relasjoner.harForeldreAnsvar"
					label="Har foreldreansvar"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'relasjoner.harForeldreAnsvar',
							'Har foreldreansvar',
						)
					}
				/>
				<FormCheckbox
					name="relasjoner.harDeltBosted"
					label="Har delt bosted"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'relasjoner.harDeltBosted',
							'Har delt bosted',
						)
					}
				/>
				<FormCheckbox
					name="relasjoner.harVergemaalEllerFremtidsfullmakt"
					label="Har vergemål eller fremtidsfullmakt"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'relasjoner.harVergemaalEllerFremtidsfullmakt',
							'Har vergemål eller fremtidsfullmakt',
						)
					}
				/>
				<FormCheckbox
					name="relasjoner.borMedMor"
					label="Bor med mor"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.borMedMor', 'Bor med mor')
					}
				/>
				<FormCheckbox
					name="relasjoner.borMedFar"
					label="Bor med far"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'relasjoner.borMedFar', 'Bor med far')
					}
				/>
				<FormCheckbox
					name="relasjoner.borMedMedmor"
					label="Bor med medmor"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'relasjoner.borMedMedmor',
							'Bor med medmor',
						)
					}
				/>
				<FormCheckbox
					name="relasjoner.foreldreHarSammeAdresse"
					label="Foreldre har samme adresse"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'relasjoner.foreldreHarSammeAdresse',
							'Foreldre har samme adresse',
						)
					}
				/>
			</div>
		</SoekKategori>
	)
}
