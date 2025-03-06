import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialFoedsel } from '@/components/fagsystem/pdlf/form/initialValues'
import React, { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'

type FoedselsdatoTypes = {
	formMethods: UseFormReturn
	path?: string
}

export const FoedselsdatoForm = ({ formMethods, path }: FoedselsdatoTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

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
				isDisabled={(foedselsdato !== null && foedselsaar === null) || harAlder()}
			/>
			<AvansertForm
				path={path}
				kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
			/>
		</>
	)
}

export const Foedselsdato = ({ formMethods }: FoedselsdatoTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.foedselsdato'}
				header="Fødselsdato"
				newEntry={getInitialFoedsel(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					return <FoedselsdatoForm formMethods={formMethods} path={path} />
				}}
			</FormDollyFieldArray>
		</div>
	)
}
