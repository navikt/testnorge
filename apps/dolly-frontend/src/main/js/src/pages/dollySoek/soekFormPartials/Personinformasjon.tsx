import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import React, { SyntheticEvent } from 'react'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { personPath } from '../SoekForm'
import { useFormContext } from 'react-hook-form'

export const Personinformasjon = ({ handleChange, setRequest }: any) => {
	const formMethods = useFormContext()
	const { watch, reset } = formMethods
	const values = watch()

	return (
		<SoekKategori>
			<FormSelect
				classNamePrefix="select-kjoenn"
				name={`${personPath}.kjoenn`}
				options={Options('kjoenn')}
				size="small"
				placeholder="Velg kjønn ..."
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'kjoenn')}
			/>
			<FormSelect
				name={`${personPath}.statsborgerskap`}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				size="large"
				placeholder="Velg statsborgerskap ..."
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'statsborgerskap')}
			/>
			<FormSelect
				name={`${personPath}.personStatus`}
				options={Options('personstatus')}
				size="medium"
				placeholder="Velg personstatus ..."
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'personStatus')}
			/>
			<FormTextInput
				name={`${personPath}.alderFom`}
				placeholder="Skriv inn alder f.o.m ..."
				type="number"
				value={watch(`${personPath}.alderFom`)}
				onBlur={(val: SyntheticEvent) => handleChange(val?.target?.value || null, 'alderFom')}
			/>
			<FormTextInput
				name={`${personPath}.alderTom`}
				placeholder="Skriv inn alder t.o.m ..."
				type="number"
				value={watch(`${personPath}.alderTom`)}
				onBlur={(val: SyntheticEvent) => handleChange(val?.target?.value || null, 'alderTom')}
			/>
			<FormCheckbox
				name={`${personPath}.erLevende`}
				label="Er levende"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'erLevende')}
				disabled={watch(`${personPath}.erDoed`)}
			/>
			<FormCheckbox
				name={`${personPath}.erDoed`}
				label="Er død"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'erDoed')}
				disabled={watch(`${personPath}.erLevende`)}
			/>
			<FormCheckbox
				data-testid={TestComponentSelectors.TOGGLE_HAR_VERGE}
				name={`${personPath}.harVerge`}
				label="Har verge"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harVerge')}
			/>
			<FormCheckbox
				name={`${personPath}.harInnflytting`}
				label="Har innflytting"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harInnflytting')}
			/>
			<FormCheckbox
				name={`${personPath}.harUtflytting`}
				label="Har utflytting"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harUtflytting')}
			/>
			<FormCheckbox
				name={`${personPath}.harSikkerhetstiltak`}
				label="Har sikkerhetstiltak"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harSikkerhetstiltak')}
			/>
			<FormCheckbox
				name={`${personPath}.harTilrettelagtKommunikasjon`}
				label="Har tilrettelagt kommunikasjon"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, 'harTilrettelagtKommunikasjon')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harSkjerming`}
				label="Har skjerming / er egen ansatt"
				checked={watch('registreRequest')?.includes('SKJERMING')}
				onChange={(val: SyntheticEvent) => {
					const registreValues = val.target.checked
						? [...watch('registreRequest'), 'SKJERMING']
						: watch('registreRequest')?.filter((item: string) => item !== 'SKJERMING')
					const updatedRequest = {
						...values,
						registreRequest: registreValues,
						side: 0,
						seed: null,
					}
					reset(updatedRequest)
					setRequest(updatedRequest)
				}}
			/>
			<div style={{ marginLeft: '-20px', marginTop: '3px' }}>
				<Hjelpetekst>Tilsvarer søk på Skjermingsregisteret under fagsystemer.</Hjelpetekst>
			</div>
		</SoekKategori>
	)
}
