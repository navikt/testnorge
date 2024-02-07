import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React, { SyntheticEvent } from 'react'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const FolkeregisteretRelasjoner = ({
	formikBag,
	handleChange,
	handleChangeBoolean,
	getValue,
}: any) => {
	const { domain: relasjonOptions } = useTenorDomain('Relasjon')

	return (
		<SoekKategori>
			<FormikSelect
				name="relasjoner.relasjon"
				options={createOptions(relasjonOptions?.data)}
				// size="medium"
				label="Relasjon"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'relasjoner.relasjon')}
				value={getValue('relasjoner.relasjon')}
			/>
			<FormikTextInput
				name="relasjoner.antallBarn.fraOgMed"
				label="Antall barn f.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'relasjoner.antallBarn.fraOgMed')
				}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikTextInput
				name="relasjoner.antallBarn.tilOgMed"
				label="Antall barn t.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'relasjoner.antallBarn.tilOgMed')
				}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikCheckbox
				name="relasjoner.harForeldreAnsvar"
				label="Har foreldreansvar"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.harForeldreAnsvar')
				}
				value={getValue('relasjoner.harForeldreAnsvar')}
			/>
			<FormikTextInput
				name="relasjoner.relasjonMedFoedselsaar.fraOgMed"
				label="Relasjon med fødselsår f.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'relasjoner.relasjonMedFoedselsaar.fraOgMed')
				}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikTextInput
				name="relasjoner.relasjonMedFoedselsaar.tilOgMed"
				label="Relasjon med fødselsår t.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'relasjoner.relasjonMedFoedselsaar.tilOgMed')
				}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikCheckbox
				name="relasjoner.harDeltBosted"
				label="Har delt bosted"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.harDeltBosted')
				}
				value={getValue('relasjoner.harDeltBosted')}
			/>
			<FormikCheckbox
				name="relasjoner.harVergemaalEllerFremtidsfullmakt"
				label="Har vergemål eller fremtidsfullmakt"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.harVergemaalEllerFremtidsfullmakt')
				}
				value={getValue('relasjoner.harVergemaalEllerFremtidsfullmakt')}
			/>
			<FormikCheckbox
				name="relasjoner.borMedMor"
				label="Bor med mor"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.borMedMor')
				}
				value={getValue('relasjoner.borMedMor')}
			/>
			<FormikCheckbox
				name="relasjoner.borMedFar"
				label="Bor med far"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.borMedFar')
				}
				value={getValue('relasjoner.borMedFar')}
			/>
			<FormikCheckbox
				name="relasjoner.borMedMedmor"
				label="Bor med medmor"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.borMedMedmor')
				}
				value={getValue('relasjoner.borMedMedmor')}
			/>
			<FormikCheckbox
				name="relasjoner.foreldreHarSammeAdresse"
				label="Foleldre har samme adresse"
				onChange={(val: SyntheticEvent) =>
					handleChangeBoolean(val?.target?.checked, 'relasjoner.foreldreHarSammeAdresse')
				}
				value={getValue('relasjoner.foreldreHarSammeAdresse')}
			/>
		</SoekKategori>
	)
}
