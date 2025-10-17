import React, { useEffect, useState } from 'react'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '@/config/kodeverk'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialTelefonnummer } from '@/components/fagsystem/pdlf/form/initialValues'
import styled from 'styled-components'
import { lookup } from 'country-data-list'

import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { Option } from '@/service/SelectOptionsOppslag'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface TelefonnummerProps {
	formMethods: UseFormReturn
	path: string
}

const StyledAvansert = styled.div`
	&&& {
		button {
			position: inherit;
		}
	}
`

const paths = {
	pdlTelefonnummer: 'pdldata.person.telefonnummer',
}

export const TelefonnummerFormRedigering = ({ path }: TelefonnummerProps) => {
	return (
		<>
			<FormSelect
				name={`${path}.landskode`}
				label="Landkode"
				kodeverk={PersoninformasjonKodeverk.Retningsnumre}
				size="large"
				isClearable={false}
			/>
			<FormTextInput name={`${path}.nummer`} label="Telefonnummer" size="large" />
			<FormSelect
				name={`${path}.prioritet`}
				label="Prioritet"
				options={[
					{ value: 1, label: '1' },
					{ value: 2, label: '2' },
				]}
				size="xsmall"
				isClearable={false}
			/>
			<StyledAvansert>
				<AvansertForm path={path} kanVelgeMaster={false} />
			</StyledAvansert>
		</>
	)
}

export const TelefonnummerForm = ({ path, formMethods }: TelefonnummerProps) => {
	const { kodeverk: landkoder, loading } = useKodeverk(AdresseKodeverk.ArbeidOgInntektLand)
	const [land, setLand] = useState(formMethods.watch(`${path}.land`) || 'NO')
	const tlfListe = formMethods.watch(path || 'pdldata.person.telefonnummer')
	const mergedeLandkoder = landkoder?.map((landkode: Option) => {
		const lookupLand = lookup.countries({ alpha2: landkode.value })?.[0]
		return {
			countryCallingCodes: lookupLand?.countryCallingCodes[0],
			emoji: lookupLand?.emoji,
			...landkode,
		}
	})
	const telefonLandkoder = SelectOptionsFormat.formatOptions('telefonLandkoder', mergedeLandkoder)

	useEffect(() => {
		if (tlfListe && tlfListe.length === 1) {
			formMethods.setValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
			formMethods.trigger(path)
		}
	}, [tlfListe])

	if (!tlfListe) {
		return null
	}

	const optionsPrioritet = () => {
		if (tlfListe.length === 1) {
			return [{ value: 1, label: '1' }]
		} else {
			return [
				{ value: 1, label: '1' },
				{ value: 2, label: '2' },
			]
		}
	}

	const handleChangeLandkode = (option: any) => {
		setLand(option.value)
		formMethods.setValue(`${path}.landskode`, option.landkode)
		formMethods.setValue(`${path}.land`, option.value)
		formMethods.trigger(path)
	}

	const handleChangeNummer = (target: { value: string }) => {
		formMethods.setValue(`${path}.nummer`, target.value)
		formMethods.trigger(path)
	}

	const handleChangePrioritet = (value: number) => {
		formMethods.setValue(`${path}.prioritet`, value)
		formMethods.trigger(path)
	}

	return (
		<>
			<DollySelect
				name={`${path}.landskode`}
				label="Landkode"
				isLoading={loading}
				value={land}
				options={telefonLandkoder}
				onChange={(option: Option) => handleChangeLandkode(option)}
				size="xlarge"
				isClearable={false}
			/>
			<DollyTextInput
				name={`${path}.nummer`}
				label="Telefonnummer"
				onChange={({ target }: { target: { value: string } }) => handleChangeNummer(target)}
				value={formMethods.watch(`${path}.nummer`)}
				size="medium"
			/>
			<FormSelect
				name={`${path}.prioritet`}
				label="Prioritet"
				options={optionsPrioritet()}
				onChange={({ value }: { value: number }) => handleChangePrioritet(value)}
				size="xsmall"
				isClearable={false}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Telefonnummer = ({ formMethods, path }: TelefonnummerProps) => {
	const tlfListe = formMethods.watch(path || paths.pdlTelefonnummer)

	if (!tlfListe) {
		return null
	}

	const handleNewEntry = (append: (value: any) => void, _values: any[]) => {
		append(initialTelefonnummer)
	}

	const handleRemoveEntry = () => {
		const oppdatertListe = formMethods.watch(paths.pdlTelefonnummer) || []
		if (oppdatertListe[0]) {
			formMethods.setValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
			formMethods.trigger(path)
		}
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={path || paths.pdlTelefonnummer}
				header="Telefonnummer"
				newEntry={initialTelefonnummer}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
				maxEntries={2}
				maxReachedDescription={'En person kan maksimalt ha to telefonnumre'}
			>
				{(tlfPath: string, idx: number) => (
					<TelefonnummerForm path={tlfPath} formMethods={formMethods} idx={idx} />
				)}
			</FormDollyFieldArray>
		</div>
	)
}
