import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { getInitialFoedested } from '@/components/fagsystem/pdlf/form/initialValues'
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

type FoedestedTypes = {
	formMethods: UseFormReturn
	path?: string
}

export const FoedestedForm = ({ formMethods, path }: FoedestedTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const handleLandChange = (selected: SelectedValue, foededtedPath: string) => {
		formMethods.setValue(`${foededtedPath}.foedeland`, selected?.value || null)
		if (selected?.value !== 'NOR') {
			formMethods.setValue(`${foededtedPath}.foedekommune`, null)
		}
		formMethods.trigger(path)
	}

	return (
		<>
			<FormTextInput name={`${path}.foedested`} label="Fødested" />
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

export const Foedested = ({ formMethods }: FoedestedTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return (
		<div className="flexbox--flex-wrap" style={{ marginBottom: '10px' }}>
			<FormDollyFieldArray
				name={'pdldata.person.foedested'}
				header="Fødested"
				newEntry={getInitialFoedested(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					return <FoedestedForm formMethods={formMethods} path={path} />
				}}
			</FormDollyFieldArray>
		</div>
	)
}
