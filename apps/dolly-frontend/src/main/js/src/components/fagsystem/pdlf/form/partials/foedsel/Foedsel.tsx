import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialFoedsel } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectedValue } from '@/components/fagsystem/pdlf/PdlTypes'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { getYearRangeOptions } from '@/utils/DataFormatter'

type FoedselTypes = {
	formMethods: UseFormReturn
	path?: string
}

export const FoedselForm = ({ formMethods, path }: FoedselTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const handleLandChange = (selected: SelectedValue, foedselPath: string) => {
		formMethods.setValue(`${foedselPath}.foedeland`, selected?.value || null)
		if (selected?.value !== 'NOR') {
			formMethods.setValue(`${foedselPath}.foedekommune`, null)
		}
		formMethods.trigger(path)
	}

	const foedselsaar = formMethods.watch(`${path}.foedselsaar`)
	const foedselsdato = formMethods.watch(`${path}.foedselsdato`)

	const minDateFoedsel =
		opts?.identtype === 'NPID' ? new Date('01.01.1870') : new Date('01.01.1900')

	const harAlder = () => {
		return (
			formMethods.watch('pdldata.opprettNyPerson.alder') ||
			formMethods.watch('pdldata.opprettNyPerson.foedtEtter') ||
			formMethods.watch('pdldata.opprettNyPerson.foedtFoer')
		)
	}

	return (
		<>
			<FormDatepicker
				name={`${path}.foedselsdato`}
				label="Fødselsdato"
				disabled={(foedselsaar !== null && foedselsdato === null) || harAlder()}
				maxDate={new Date()}
				minDate={minDateFoedsel}
			/>
			<FormSelect
				name={`${path}.foedselsaar`}
				label="Fødselsår"
				options={getYearRangeOptions(minDateFoedsel, new Date())}
				isDisabled={
					(foedselsdato !== null && foedselsdato !== '' && foedselsaar === null) || harAlder()
				}
			/>
			<FormTextInput name={`${path}.foedested`} label="Fødested" size="large" />
			<FormSelect
				name={`${path}.foedekommune`}
				label="Fødekommune"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
				isDisabled={
					formMethods.watch(`${path}.foedeland`) !== 'NOR' &&
					formMethods.watch(`${path}.foedeland`) !== null
				}
			/>
			<FormSelect
				name={`${path}.foedeland`}
				label="Fødeland"
				onChange={(selected: SelectedValue) => handleLandChange(selected, path)}
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
			/>
			<AvansertForm
				path={path}
				kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
			/>
		</>
	)
}

export const Foedsel = ({ formMethods }: FoedselTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.foedsel'}
				header="Fødsel"
				newEntry={getInitialFoedsel(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					return <FoedselForm formMethods={formMethods} path={path} />
				}}
			</FormDollyFieldArray>
		</div>
	)
}
