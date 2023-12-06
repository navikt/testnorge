import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialFoedsel } from '@/components/fagsystem/pdlf/form/initialValues'
import { Yearpicker } from '@/components/ui/form/inputs/yearpicker/Yearpicker'
import * as _ from 'lodash'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectedValue } from '@/components/fagsystem/pdlf/PdlTypes'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

type FoedselTypes = {
	formMethods: UseFormReturn
	path?: string
}

export const FoedselForm = ({ formMethods, path }: FoedselTypes) => {
	const opts = useContext(BestillingsveilederContext)

	const handleLandChange = (selected: SelectedValue, foedselPath: string) => {
		formMethods.setValue(`${foedselPath}.foedeland`, selected?.value || null)
		if (selected?.value !== 'NOR') {
			formMethods.setValue(`${foedselPath}.foedekommune`, null)
		}
	}

	const foedselsaar = _.get(formMethods.getValues(), `${path}.foedselsaar`)
	const foedselsdato = _.get(formMethods.getValues(), `${path}.foedselsdato`)

	const minDateFoedsel =
		opts?.identtype === 'NPID' ? new Date('01.01.1870') : new Date('01.01.1900')

	const harAlder = () => {
		return (
			_.get(formMethods.getValues(), 'pdldata.opprettNyPerson.alder') ||
			_.get(formMethods.getValues(), 'pdldata.opprettNyPerson.foedtEtter') ||
			_.get(formMethods.getValues(), 'pdldata.opprettNyPerson.foedtFoer')
		)
	}

	return (
		<>
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.foedselsdato`}
					label="Fødselsdato"
					disabled={(foedselsaar !== null && foedselsdato === null) || harAlder()}
					maxDate={new Date()}
					minDate={minDateFoedsel}
				/>
				<Yearpicker
					formMethods={formMethods}
					name={`${path}.foedselsaar`}
					label="Fødselsår"
					date={foedselsaar ? new Date(foedselsaar, 0) : null}
					handleDateChange={(val) =>
						formMethods.setValue(`${path}.foedselsaar`, val ? new Date(val).getFullYear() : null)
					}
					maxDate={new Date()}
					minDate={minDateFoedsel}
					// @ts-ignore
					disabled={(foedselsdato !== null && foedselsaar === null) || harAlder()}
				/>
			</DatepickerWrapper>
			<FormikTextInput name={`${path}.foedested`} label="Fødested" size="large" />
			<FormikSelect
				name={`${path}.foedekommune`}
				label="Fødekommune"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
				isDisabled={
					_.get(formMethods.getValues(), `${path}.foedeland`) !== 'NOR' &&
					_.get(formMethods.getValues(), `${path}.foedeland`) !== null
				}
			/>
			<FormikSelect
				name={`${path}.foedeland`}
				label="Fødeland"
				onChange={(selected: SelectedValue) => handleLandChange(selected, path)}
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Foedsel = ({ formMethods }: FoedselTypes) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.foedsel'}
				header="Fødsel"
				newEntry={getInitialFoedsel(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					return <FoedselForm formMethods={formMethods} path={path} />
				}}
			</FormikDollyFieldArray>
		</div>
	)
}
