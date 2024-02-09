import React, { useEffect, useState } from 'react'
import { useBoolean } from 'react-use'
import * as _ from 'lodash-es'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PdlforvalterApi } from '@/service/Api'
import { useFormikContext } from 'formik'
import { useNaviger } from '@/utils/hooks/useNaviger'

type ArbeidsgiverIdentProps = {
	path: string
	isDisabled?: boolean
}

export const ArbeidsgiverIdent = ({ path, isDisabled }: ArbeidsgiverIdentProps) => {
	const formikBag = useFormikContext()
	const [error, setError] = useState(null)
	// const [success, setSuccess] = useBoolean(false)
	const [personnummer, setPersonnummer] = useState(_.get(formikBag.values, path))
	const [success, setSuccess] = useBoolean(personnummer && !error)
	const { result, loading: loadingNaviger, error: errorNaviger, mutate } = useNaviger(personnummer)

	useEffect(() => {
		if (personnummer) {
			if (result?.identNavigerTil) {
				setSuccess(true)
				setError(null)
			} else {
				setError('Fant ikke arbeidsgiver-ident')
			}
		}
	}, [result, errorNaviger])

	useEffect(() => {
		if (error) {
			formikBag.setFieldError(path, error)
		} else {
			formikBag.setFieldError(path, undefined)
		}
	}, [error, formikBag.errors, personnummer])

	const handleChange = (event: React.ChangeEvent<any>) => {
		// event.preventDefault()
		setError(null)
		setSuccess(false)

		const personnr = event.target.value
		formikBag.setFieldValue(`${path}`, personnr)

		if (personnr.match(/^\d{11}$/) != null) {
			setPersonnummer(personnr)
			// formikBag.setFieldValue(`${path}`, personnr)
			// setValgtIdent(personnr)
		} else {
			setError('Ident må være et tall med 11 siffer')
			// formikBag.setFieldValue(path, '')
		}
	}

	const getFeilmelding = () => {
		if (error) {
			return {
				feilmelding: error,
			}
		} else if (!_.get(formikBag.values, path)) {
			return { feilmelding: 'Feltet er påkrevd' }
		}
		return null
	}

	const feilmelding = getFeilmelding()

	return (
		<div>
			<DollyTextInput
				name={path}
				// @ts-ignore
				defaultValue={personnummer}
				label={'Arbeidsgiver ident'}
				onBlur={handleChange}
				isDisabled={loadingNaviger || isDisabled}
				feil={feilmelding}
			/>
			{success && (
				<div className="flexbox" style={{ marginTop: '-5px' }}>
					<Icon size={22} kind="feedback-check-circle" style={{ marginRight: '10px' }} /> Ident
					funnet
				</div>
			)}
			{loadingNaviger && <Loading label="Sjekker arbeidsgiver ident." />}
		</div>
	)
}
