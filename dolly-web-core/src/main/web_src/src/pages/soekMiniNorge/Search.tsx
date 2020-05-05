import React, { Fragment } from 'react'
import {SearchOptions} from '~/pages/soekMiniNorge/SearchOptions'
import { Formik } from 'formik'
import { stateModifierFns } from '~/components/bestillingsveileder/stateModifier'

export const Search = ()=>{
	const _onSubmit = (values: any) => {}
	const initialValues = {
		navn: {
			fornavn: '',
			mellomnavn: '',
			etternavn: ''
		}
	}

	return (
		<Formik onSubmit={_onSubmit} initialValues={initialValues} enableReinitialize>
			{formikBag => {
				const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues)
				return(
					<Fragment>
						<SearchOptions formikBag={formikBag}/>
					</Fragment>
				)
			}}
		</Formik>
	)

}