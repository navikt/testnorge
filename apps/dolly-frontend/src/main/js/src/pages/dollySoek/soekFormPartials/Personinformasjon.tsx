import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import React, { SyntheticEvent } from 'react'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { personPath } from '../SoekForm'
import { useFormContext } from 'react-hook-form'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export const Personinformasjon = ({
	handleChange,
	setRequest,
	lagreSoekRequest,
	setLagreSoekRequest,
}: any) => {
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
				onChange={(val: SyntheticEvent) =>
					handleChange(
						(val as any)?.value || null,
						`${personPath}.kjoenn`,
						`Kjønn: ${codeToNorskLabel(val?.value)}`,
					)
				}
			/>
			<FormSelect
				name={`${personPath}.statsborgerskap`}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				size="large"
				placeholder="Velg statsborgerskap ..."
				onChange={(val: SyntheticEvent) =>
					handleChange((val as any)?.value || null,
						`${personPath}.statsborgerskap`,
						`Statsborgerskap: ${val?.value}`,
					)
				}
			/>
			<FormTextInput
            				name={`${personPath}.antallStatsborgerskap`}
            				placeholder="Skriv inn antall statsborgerskap ..."
            				type="number"
            				onBlur={(val: SyntheticEvent) =>
            					handleChange(
            						val?.target?.value || null,
            						`${personPath}.antallStatsborgerskap`,
            						`Antall statsborgerskap: ${val?.target?.value}`,
            					)
            				}
            			/>
			<FormSelect
				name={`${personPath}.personStatus`}
				options={Options('personstatus')}
				size="medium"
				placeholder="Velg personstatus ..."
				onChange={(val: SyntheticEvent) =>
					handleChange((val as any)?.value || null,
						`${personPath}.personStatus`,
						`Personstatus: ${codeToNorskLabel(val?.value)}`,
					)
				}
			/>
			<FormTextInput
				name={`${personPath}.alderFom`}
				useControlled={true}
				placeholder="Skriv inn alder f.o.m ..."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(
						val?.target?.value || null,
						`${personPath}.alderFom`,
						`Alder f.o.m.: ${val?.target?.value}`,
					)
				}
			/>
			<FormTextInput
				name={`${personPath}.alderTom`}
				placeholder="Skriv inn alder t.o.m ..."
				type="number"
				onBlur={(val: any) =>
					handleChange(
						val?.target?.value || null,
						`${personPath}.alderTom`,
						`Alder t.o.m.: ${val?.target?.value}`,
					)
				}
			/>
			<FormCheckbox
				name={`${personPath}.erLevende`}
				label="Er levende"
				onChange={(val: any) =>
					handleChange(val.target.checked, `${personPath}.erLevende`, 'Er levende')
				}
				disabled={watch(`${personPath}.erDoed`)}
			/>
			<FormCheckbox
				name={`${personPath}.erDoed`}
				label="Er død"
				onChange={(val: any) =>
					handleChange(val.target.checked, `${personPath}.erDoed`, 'Er død')
				}
				disabled={watch(`${personPath}.erLevende`)}
			/>
			<FormCheckbox
				data-testid={TestComponentSelectors.TOGGLE_HAR_VERGE}
				name={`${personPath}.harVerge`}
				label="Har verge"
				onChange={(val: any) =>
					handleChange(val.target.checked, `${personPath}.harVerge`, 'Har verge')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harInnflytting`}
				label="Har innflytting"
				onChange={(val: any) =>
					handleChange(val.target.checked, `${personPath}.harInnflytting`, 'Har innflytting')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harUtflytting`}
				label="Har utflytting"
				onChange={(val: any) =>
					handleChange(val.target.checked, `${personPath}.harUtflytting`, 'Har utflytting')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harSikkerhetstiltak`}
				label="Har sikkerhetstiltak"
				onChange={(val: any) =>
					handleChange(
						val.target.checked,
						`${personPath}.harSikkerhetstiltak`,
						'Har sikkerhetstiltak',
					)
				}
			/>
			<FormCheckbox
				name={`${personPath}.harTilrettelagtKommunikasjon`}
				label="Har tilrettelagt kommunikasjon"
				onChange={(val: any) => handleChange(val.target.checked, `${personPath}.harTilrettelagtKommunikasjon`,
						'Har tilrettelagt kommunikasjon',
					)}
			/>
			<FormCheckbox
				name={`${personPath}.harSkjerming`}
				label="Har skjerming / er egenansatt"
				checked={watch('registreRequest')?.includes('SKJERMING')}
				onChange={(val: any) => {
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
					let registreSoekValues = lagreSoekRequest?.registreRequest || []
					if (val.target.checked) {
						registreSoekValues.push({
							path: 'registreRequest',
							value: 'SKJERMING',
							label: 'Fagsystem: Skjermingsregisteret',
						})
					} else {
						registreSoekValues = registreSoekValues.filter(
							(item: string) => item.value !== 'SKJERMING',
						)
					}
					setLagreSoekRequest({ ...lagreSoekRequest, registreRequest: registreSoekValues })
				}}
			/>
			<div style={{ marginLeft: '-20px', marginTop: '3px' }}>
				<Hjelpetekst>Tilsvarer søk på Skjermingsregisteret under fagsystemer.</Hjelpetekst>
			</div>
		</SoekKategori>
	)
}
