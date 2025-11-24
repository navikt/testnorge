import React, { useContext } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface PdlNyPersonValues {
	nyPersonPath: string
	eksisterendePersonPath?: string
	formMethods: UseFormReturn
	erNyIdent?: boolean
	gruppeIdenter?: Array<string>
	eksisterendeNyPerson?: Option
}

export const PdlNyPerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	formMethods,
	erNyIdent = false,
	gruppeIdenter,
	eksisterendeNyPerson = null,
}: PdlNyPersonValues) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const isLeggTil = opts?.is?.leggTil
	const disableAlder =
		formMethods.watch(`${nyPersonPath}.foedtEtter`) != null ||
		formMethods.watch(`${nyPersonPath}.foedtFoer`) != null

	const disableFoedtDato = !['', null].includes(formMethods.watch(`${nyPersonPath}.alder`))

	const getIdenttypeOptions = () => {
		if (erNyIdent && isLeggTil) {
			const currentIdenttype = opts?.personFoerLeggTil?.pdlforvalter?.person?.identtype
			if (currentIdenttype === 'DNR') {
				return Options('identtype').filter((a) => a.value !== 'NPID')
			}
			if (currentIdenttype === 'FNR') {
				return Options('identtype').filter((a) => a.value !== 'NPID' && a.value !== 'DNR')
			}
		}
		return Options('identtype')
	}

	const identtypeOptions = getIdenttypeOptions()

	const eksisterendePerson = eksisterendePersonPath && formMethods.watch(eksisterendePersonPath)

	const hasEksisterendePerson =
		eksisterendePerson &&
		(gruppeIdenter?.includes(eksisterendePerson) ||
			eksisterendePerson === eksisterendeNyPerson?.value ||
			formMethods.watch('vergemaal.vergeIdent') === eksisterendeNyPerson?.value ||
			formMethods.watch('sivilstand.relatertVedSivilstand') === eksisterendeNyPerson?.value)

	return (
		<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
			<FormSelect
				name={`${nyPersonPath}.identtype`}
				label="Identtype"
				options={identtypeOptions}
				isDisabled={hasEksisterendePerson}
			/>
			<FormSelect
				name={`${nyPersonPath}.kjoenn`}
				label="Kjønn"
				options={Options('kjoenn')}
				isDisabled={hasEksisterendePerson}
			/>
			<FormTextInput
				name={`${nyPersonPath}.alder`}
				type="number"
				label="Alder"
				isDisabled={disableAlder || hasEksisterendePerson}
			/>
			<FormDatepicker
				name={`${nyPersonPath}.foedtEtter`}
				label="Født etter"
				disabled={disableFoedtDato || hasEksisterendePerson}
				maxDate={new Date()}
			/>
			<FormDatepicker
				name={`${nyPersonPath}.foedtFoer`}
				label="Født før"
				disabled={disableFoedtDato || hasEksisterendePerson}
				maxDate={new Date()}
			/>
			{!erNyIdent && (
				<FormSelect
					name={`${nyPersonPath}.statsborgerskapLandkode`}
					label="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					size="large"
					isDisabled={hasEksisterendePerson}
				/>
			)}
			{!erNyIdent && (
				<FormSelect
					name={`${nyPersonPath}.gradering`}
					label="Gradering"
					options={Options('gradering')}
					isDisabled={hasEksisterendePerson}
					size={'large'}
				/>
			)}
			<FormCheckbox
				name={`${nyPersonPath}.nyttNavn.hasMellomnavn`}
				id={`${nyPersonPath}.nyttNavn.hasMellomnavn`}
				label="Har mellomnavn"
				disabled={hasEksisterendePerson}
				checkboxMargin={true}
			/>
		</div>
	)
}
