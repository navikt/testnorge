import React from 'react'
import { NavnForm } from '~/components/fagsystem/pdlf/form/partials/navn/Navn'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { KjoennForm } from '~/components/fagsystem/pdlf/form/partials/kjoenn/Kjoenn'
import { PersonstatusForm } from '~/components/fagsystem/pdlf/form/partials/personstatus/Personstatus'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const PersondetaljerSamlet = ({ path, idx, formikBag }) => {
	// console.log('path: ', path) //TODO - SLETT MEG
	// console.log('formikBag: ', formikBag) //TODO - SLETT MEG
	return (
		<>
			<FormikTextInput
				// name={`${path}.ident`}
				name="ident"
				label="Ident"
				disabled={true}
				title="Ident kan ikke redigeres"
			/>
			{/*<NavnForm path={`${path}.navn[0]`} />*/}
			<NavnForm formikBag={formikBag} path="navn[0]" />
			<div className="flexbox--full-width" style={{ marginTop: '10px' }}>
				{/*<KjoennForm path={`${path}.kjoenn[0]`} />*/}
				<KjoennForm path="kjoenn[0]" />
			</div>
			<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
				{/*<PersonstatusForm path={`${path}.folkeregisterPersonstatus[0]`} />*/}
				<PersonstatusForm path="folkeregisterpersonstatus[0]" />
			</div>
			{/*<FormikDatepicker*/}
			{/*	name={path}*/}
			{/*	label="Skjerming fra"*/}
			{/*	disabled={true}*/}
			{/*	title="Skjerming kan ikke redigeres"*/}
			{/*/>*/}
			{/*<FormikDatepicker*/}
			{/*	name={path}*/}
			{/*	label="Skjerming til"*/}
			{/*	disabled={true}*/}
			{/*	title="Skjerming kan ikke redigeres"*/}
			{/*/>*/}
			{/*<FormikSelect*/}
			{/*	name={path}*/}
			{/*	label="Språk"*/}
			{/*	kodeverk={PersoninformasjonKodeverk.Spraak}*/}
			{/*	size="large"*/}
			{/*	disabled={true}*/}
			{/*	title="Språk kan ikke redigeres"*/}
			{/*/>*/}
		</>
	)
}
