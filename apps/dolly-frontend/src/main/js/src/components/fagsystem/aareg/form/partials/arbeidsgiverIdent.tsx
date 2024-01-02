import React, { useState } from 'react'
import { useBoolean } from 'react-use'
import * as _ from 'lodash'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PdlforvalterApi } from '@/service/Api'
import { useFormContext } from 'react-hook-form'

type ArbeidsgiverIdentProps = {
	path: string
	isDisabled?: boolean
}

export const ArbeidsgiverIdent = ({ path, isDisabled }: ArbeidsgiverIdentProps) => {
	const formMethods = useFormContext()
	const [error, setError] = useState(null)
	const [personnummer, setPersonnummer] = useState(_.get(formMethods.getValues(), path))
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)

	const handleChange = (event: React.ChangeEvent<any>) => {
		event.preventDefault()
		setError(null)
		setSuccess(false)

		const personnr = event.target.value

		if (personnr.match(/^\d{11}$/) != null) {
			handleManualPersonnrChange(personnr)
		}
	}

	const handleManualPersonnrChange = (personnr: string) => {
		setLoading(true)

		PdlforvalterApi.getPersoner([personnr])
			.then((response: any) => {
				if (!response?.data || response?.data?.length < 1) {
					setError('Fant ikke arbeidsgiver-ident')
					setLoading(false)
					formMethods.setValue(`${path}`, '')
					return
				}

				setError(null)
				setSuccess(true)
				setLoading(false)
				setPersonnummer(personnr)

				formMethods.setValue(`${path}`, personnr)
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
			/>
			{success && (
				<div className="flexbox" style={{ marginTop: '-5px' }}>
					<Icon size={22} kind="feedback-check-circle" style={{ marginRight: '10px' }} /> Ident
					funnet
				</div>
			)}
			{loading && <Loading label="Sjekker arbeidsgiver ident." />}
			{error && <div className="skjemaelement__feilmelding">{error}</div>}
		</div>
	)
}
