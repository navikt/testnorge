import React, { useState } from 'react'
import { useBoolean } from 'react-use'
import * as _ from 'lodash-es'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PdlforvalterApi } from '@/service/Api'
import { useFormikContext } from 'formik'

type ArbeidsgiverIdentProps = {
	path: string
	isDisabled?: boolean
}

export const ArbeidsgiverIdent = ({ path, isDisabled }: ArbeidsgiverIdentProps) => {
	const formikBag = useFormikContext()
	const [error, setError] = useState(null)
	const [personnummer, setPersonnummer] = useState(_.get(formikBag.values, path))
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)

	const handleChange = (event: React.ChangeEvent<any>) => {
		event.preventDefault()
		setError(null)
		setSuccess(false)

		const personnr = event.target.value

		// TODO: move to frontend validation
		if (personnr.match(/^\d{11}$/) != null) {
			handleManualPersonnrChange(personnr)
		} else {
			setError('Ident må være et tall med 11 siffer')
			formikBag.setFieldValue(`${path}`, '')
		}
	}

	const handleManualPersonnrChange = (personnr: string) => {
		setLoading(true)

		PdlforvalterApi.getPersoner([personnr])
			.then((response: any) => {
				if (!response?.data || response?.data?.length < 1) {
					setError('Fant ikke arbeidsgiver-ident')
					setLoading(false)
					formikBag.setFieldValue(`${path}`, '')
					return
				}

				setError(null)
				setSuccess(true)
				setLoading(false)
				setPersonnummer(personnr)

				formikBag.setFieldValue(`${path}`, personnr)
			})
			.catch(() => setError('Fant ikke arbeidsgiver-ident'))
	}

	return (
		<div>
			<DollyTextInput
				name={path}
				// @ts-ignore
				defaultValue={personnummer}
				label={'Arbeidsgiver ident'}
				onBlur={handleChange}
				isDisabled={loading || isDisabled}
				feil={
					error && {
						feilmelding: error,
					}
				}
			/>
			{success && (
				<div className="flexbox" style={{ marginTop: '-5px' }}>
					<Icon size={22} kind="feedback-check-circle" style={{ marginRight: '10px' }} /> Ident
					funnet
				</div>
			)}
			{loading && <Loading label="Sjekker arbeidsgiver ident." />}
		</div>
	)
}
