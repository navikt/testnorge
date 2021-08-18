import React, { useState } from 'react'
import { useBoolean } from 'react-use'
import _get from 'lodash/get'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TpsfApi } from '~/service/Api'
import { FormikProps } from 'formik'

type ArbeidsgiverIdentProps = {
	formikBag: FormikProps<{}>
	path: string
}

export const ArbeidsgiverIdent = ({ formikBag, path }: ArbeidsgiverIdentProps) => {
	const [error, setError] = useState(null)
	const [personnummer, setPersonnummer] = useState(_get(formikBag.values, path))
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [miljoer, setMiljoer] = useState(null)

	const handleChange = (event: React.ChangeEvent<any>) => {
		event.preventDefault()
		let personnr = event.target.value
		handleManualPersonnrChange(personnr)
	}

	const handlePaste = (event: React.ClipboardEvent<any>) => {
		event.preventDefault()
		let personnr = event.clipboardData.getData('Text')
		handleManualPersonnrChange(personnr)
	}

	const handleManualPersonnrChange = (personnr: string) => {
		setPersonnummer(personnr)

		setError(null)
		setMiljoer(null)
		setSuccess(false)
		setLoading(true)

		if (!personnr){
			setError('Ident må være et tall med 11 sifre')
			setLoading(false)
			formikBag.setFieldValue(`${path}`, '')
			return
		}

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

				let env = response.data.statusPaaIdenter[0].env
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
				onPaste={handlePaste}
				feil={
					error && {
						feilmelding: error
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
