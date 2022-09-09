import React, { useContext } from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DatepickerWrapper } from '~/components/ui/form/inputs/datepicker/DatepickerStyled'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Innflytting } from '~/components/fagsystem/pdlf/PdlTypes'
import { getSisteDato } from '~/components/bestillingsveileder/utils'

type UtvandringTypes = {
	path: string
	minDate?: Date
}

export const UtvandringForm = ({ path, minDate }: UtvandringTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.tilflyttingsland`}
				label="Utvandret til"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
				isClearable={false}
			/>
			<FormikTextInput name={`${path}.tilflyttingsstedIUtlandet`} label="Tilflyttingssted" />
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.utflyttingsdato`}
					label="Utflyttingsdato"
					minDate={minDate}
				/>
			</DatepickerWrapper>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Utvandring = () => {
	const opts = useContext(BestillingsveilederContext)

	const sisteDatoInnflytting = () => {
		if (opts.is.leggTil) {
			const innflytting = opts?.personFoerLeggTil?.pdlforvalter?.person?.innflytting
			let siste = getSisteDato(
				innflytting.map((val: Innflytting) => new Date(val.innflyttingsdato))
			)
			if (siste !== null) {
				siste.setDate(siste.getDate() + 1)
			}
			return siste
		}
		return null
	}

	const datoBegresning = sisteDatoInnflytting()
	return (
		<div className="person-visning_content">
			<UtvandringForm path={'pdldata.person.utflytting[0]'} minDate={datoBegresning} />
		</div>
	)
}
