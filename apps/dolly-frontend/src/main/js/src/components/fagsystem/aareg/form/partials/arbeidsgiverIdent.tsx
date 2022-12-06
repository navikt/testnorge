import React, { useState } from 'react'
import { useBoolean } from 'react-use'
import _get from 'lodash/get'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { TpsfApi } from '@/service/Api'
import { useFormikContext } from 'formik'

type ArbeidsgiverIdentProps = {
	path: string
	isDisabled?: boolean
}

export const ArbeidsgiverIdent = ({ path, isDisabled }: ArbeidsgiverIdentProps) => {
	const formikBag = useFormikContext()
	const [error, setError] = useState(null)
	const [personnummer, setPersonnummer] = useState(_get(formikBag.values, path))
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [miljoer, setMiljoer] = useState(null)

	const handleChange = (event: React.ChangeEvent<any>) => {
		event.preventDefault()
		setError(null)
		setMiljoer(null)
		setSuccess(false)

		const personnr = event.target.value

		// TODO: move to frontend validation
		if (personnr.match(/^\d{11}$/) != null) {
			handleManualPersonnrChange(personnr)
		} else {
			setError('Ident må være et tall med 11 siffer.')
			formikBag.setFieldValue(`${path}`, '')
		}
	}

	const handleManualPersonnrChange = (personnr: string) => {
		setLoading(true)

		TpsfApi.getMiljoerByFnr(personnr)
			.then((response: any) => {
				if (
					!('statusPaaIdenter' in response.data) ||
					response.data.statusPaaIdenter.length === 0 ||
					response.data.statusPaaIdenter[0].env.length === 0
				) {
					setError('Fant ikke arbeidsgiver ident.')
					setLoading(false)
					formikBag.setFieldValue(`${path}`, '')
					return
				}

				setError(null)
				setSuccess(true)
				setLoading(false)
				setPersonnummer(personnr)

				const env = response.data.statusPaaIdenter[0].env
				setMiljoer(env === null ? env : env.toString())

				formikBag.setFieldValue(`${path}`, personnr)
			})
			.catch(() => setError('Fant ikke arbeidsgiver ident.'))
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
				<div className="flexbox">
					<Icon size={22} kind="feedback-check-circle" /> Ident funnet i miljø: {miljoer}
				</div>
			)}
			{loading && <Loading label="Sjekker arbeidsgiver ident." />}
		</div>
	)
}
