import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialFoedsel } from '@/components/fagsystem/pdlf/form/initialValues'
import { Yearpicker } from '@/components/ui/form/inputs/yearpicker/Yearpicker'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

type FoedselsdatoTypes = {
	formMethods: UseFormReturn
	path?: string
}

export const FoedselsdatoForm = ({ formMethods, path }: FoedselsdatoTypes) => {
	const opts = useContext(BestillingsveilederContext)

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
				disabled={(foedselsaar !== null && foedselsaar !== '') || harAlder()}
				maxDate={new Date()}
				minDate={minDateFoedsel}
			/>
			<Yearpicker
				formMethods={formMethods}
				name={`${path}.foedselsaar`}
				label="Fødselsår"
				date={foedselsaar ? new Date(foedselsaar, 0) : null}
				handleDateChange={(val) => {
					formMethods.setValue(`${path}.foedselsaar`, val ? new Date(val).getFullYear() : null)
					formMethods.trigger(path)
				}}
				maxDate={new Date()}
				minDate={minDateFoedsel}
				// @ts-ignore
				disabled={(foedselsdato !== null && foedselsdato !== '') || harAlder()}
			/>
			<AvansertForm
				path={path}
				kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
			/>
		</>
	)
}

export const Foedselsdato = ({ formMethods }: FoedselsdatoTypes) => {
	const opts = useContext(BestillingsveilederContext)

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
