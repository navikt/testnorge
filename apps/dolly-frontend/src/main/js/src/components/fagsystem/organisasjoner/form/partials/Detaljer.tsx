import React, { useEffect, useMemo, useRef, useState } from 'react'
import * as _ from 'lodash-es'
import { organisasjonPaths } from '../paths'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { OrganisasjonKodeverk } from '@/config/kodeverk'
import { Kontaktdata } from './Kontaktdata'
import { Adresser } from './Adresser'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { UseFormReturn } from 'react-hook-form/dist/types'

type DetaljerProps = {
	formMethods: UseFormReturn
	path: string
	level: number
	number?: string
}

enum TypeUnderenhet {
	JURIDISKENHET = 'JURIDISKENHET',
	VIRKSOMHET = 'VIRKSOMHET',
}

const StyledToggleGroup = styled(ToggleGroup)`
	margin-bottom: 7px;
`

export const Detaljer = ({ formMethods, path, level, number }: DetaljerProps) => {
	const watchedValues = formMethods.watch(path)
	const initialValues = useMemo(
		() => _.omit(watchedValues || {}, ['underenheter', 'sektorkode', 'typeUnderenhet']),
		[watchedValues],
	)
	const createNewEntry = () => _.cloneDeep(initialValues)

	const computeTypeFromForm = () => {
		const saved = formMethods.getValues(`${path}.typeUnderenhet`)
		if (saved === TypeUnderenhet.JURIDISKENHET || saved === TypeUnderenhet.VIRKSOMHET) return saved
		if (level === 0) return TypeUnderenhet.JURIDISKENHET
		const under = formMethods.getValues(`${path}.underenheter`)
		return under && under.length > 0 ? TypeUnderenhet.JURIDISKENHET : TypeUnderenhet.VIRKSOMHET
	}

	const [typeUnderenhet, setTypeUnderenhet] = useState(computeTypeFromForm())
	const previousUnderEnheterRef = useRef<any[] | undefined>(undefined)
	const enhetstype = formMethods.watch(`${path}.enhetstype`)
	const hasChildren =
		Array.isArray(formMethods.getValues(`${path}.underenheter`)) &&
		formMethods.getValues(`${path}.underenheter`)?.length > 0

	useEffect(() => {
		const stored = formMethods.getValues(`${path}.typeUnderenhet`)
		if (!stored) formMethods.setValue(`${path}.typeUnderenhet`, typeUnderenhet)
	}, [typeUnderenhet, formMethods, path])

	const handleToggleChange = (next: TypeUnderenhet) => {
		if (next === typeUnderenhet) return
		formMethods.setValue(`${path}.typeUnderenhet`, next)
		formMethods.setValue(`${path}.enhetstype`, '')
		const sektorkodeErValgt = formMethods.getValues(`${path}.sektorkode`)
		if (next === TypeUnderenhet.VIRKSOMHET) {
			previousUnderEnheterRef.current = formMethods.getValues(`${path}.underenheter`) || []
			formMethods.setValue(`${path}.underenheter`, undefined)
			if (sektorkodeErValgt) formMethods.setValue(`${path}.sektorkode`, undefined)
		} else if (next === TypeUnderenhet.JURIDISKENHET && level < 4) {
			if (previousUnderEnheterRef.current && previousUnderEnheterRef.current.length > 0) {
				formMethods.setValue(`${path}.underenheter`, previousUnderEnheterRef.current)
			}
			if (sektorkodeErValgt) formMethods.setValue(`${path}.sektorkode`, '')
		}
		setTypeUnderenhet(next)
	}

	const mustHaveUnderenhet =
		typeUnderenhet === TypeUnderenhet.JURIDISKENHET && enhetstype && enhetstype !== 'ENK'

	return (
		<>
			<Kategori title={!number ? 'Organisasjon' : undefined} vis={organisasjonPaths} flexRow={true}>
				<div className="toggle--wrapper">
					{level > 0 && (
						<StyledToggleGroup
							size={'small'}
							onChange={(v: string) => handleToggleChange(v as TypeUnderenhet)}
							value={typeUnderenhet}
						>
							<ToggleGroup.Item
								key={TypeUnderenhet.JURIDISKENHET}
								value={TypeUnderenhet.JURIDISKENHET}
							>
								Juridisk enhet
							</ToggleGroup.Item>
							<ToggleGroup.Item
								key={TypeUnderenhet.VIRKSOMHET}
								value={TypeUnderenhet.VIRKSOMHET}
								disabled={hasChildren}
								style={hasChildren ? { opacity: 0.5 } : undefined}
								title={
									hasChildren
										? 'Kan ikke endre til virksomhet når denne enheten har underenheter. Fjern underenheter først.'
										: undefined
								}
							>
								Virksomhet
							</ToggleGroup.Item>
						</StyledToggleGroup>
					)}
					{path === 'organisasjon' ? (
						<FormSelect
							name={`${path}.enhetstype`}
							label="Enhetstype"
							kodeverk={OrganisasjonKodeverk.EnhetstyperJuridiskEnhet}
							size="xxlarge"
							isClearable={false}
						/>
					) : (
						<FormSelect
							name={`${path}.enhetstype`}
							label="Enhetstype"
							kodeverk={
								typeUnderenhet === TypeUnderenhet.JURIDISKENHET
									? OrganisasjonKodeverk.EnhetstyperJuridiskEnhet
									: OrganisasjonKodeverk.EnhetstyperVirksomhet
							}
							size="xxlarge"
							isClearable={false}
						/>
					)}
				</div>
				<FormSelect
					name={`${path}.naeringskode`}
					label="Næringskode"
					kodeverk={OrganisasjonKodeverk.Naeringskoder}
					size="xlarge"
					optionHeight={50}
					isClearable={false}
					visHvisAvhuket
				/>
				{typeUnderenhet === TypeUnderenhet.JURIDISKENHET && (
					<FormSelect
						name={`${path}.sektorkode`}
						label="Sektorkode"
						kodeverk={OrganisasjonKodeverk.Sektorkoder}
						size="xxlarge"
						isClearable={false}
						visHvisAvhuket
					/>
				)}
				<FormTextInput name={`${path}.formaal`} label="Formål" size="xlarge" />
				<FormDatepicker name={`${path}.stiftelsesdato`} label="Stiftelsesdato" />
				<FormSelect
					name={`${path}.maalform`}
					label="Målform"
					options={Options('maalform')}
					isClearable={false}
					visHvisAvhuket
				/>
			</Kategori>

			<Kontaktdata path={path} />

			<Adresser formMethods={formMethods} path={path} />

			<FormDollyFieldArray
				name={`${path}.underenheter`}
				header="Underenhet"
				newEntry={createNewEntry()}
				handleNewEntry={(appendFn: any) => appendFn(createNewEntry())}
				disabled={level > 3 || typeUnderenhet === TypeUnderenhet.VIRKSOMHET}
				title={
					level > 3
						? 'Du kan maksimalt lage fire nivåer av underenheter'
						: typeUnderenhet === TypeUnderenhet.VIRKSOMHET
							? 'Du kan ikke legge til underenheter på enhet av type virksomhet'
							: undefined
				}
				canBeEmpty={!mustHaveUnderenhet || enhetstype === 'ENK' || !enhetstype}
				tag={number}
				isOrganisasjon={true}
				maxEntries={level === 0 ? undefined : 1}
				leafOnlyDelete={true}
			>
				{(childPath: string, _idx: number, _curr: any, childNumber: string, fieldId: string) => (
					<Detaljer
						key={fieldId}
						formMethods={formMethods}
						path={childPath}
						level={level + 1}
						number={childNumber ? childNumber : (level + 1).toString()}
					/>
				)}
			</FormDollyFieldArray>
		</>
	)
}
