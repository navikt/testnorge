import Title from '../../components/title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { dollySoekLocalStorageKey, SoekForm } from '@/pages/dollySoek/SoekForm'
import { SisteSoek, soekType } from '@/components/ui/soekForm/SisteSoek'
import { useEffect, useRef, useState } from 'react'
import { useForm } from 'react-hook-form'
import * as _ from 'lodash-es'
import { dollySoekInitialValues } from '@/pages/dollySoek/dollySoekInitialValues'
import { DollyApi } from '@/service/Api'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export default () => {
	const [lagreSoekRequest, setLagreSoekRequest] = useState({})
	const lagreSoekRequestRef = useRef(lagreSoekRequest)

	useEffect(() => {
		lagreSoekRequestRef.current = lagreSoekRequest
	}, [lagreSoekRequest])

	useEffect(() => {
		return () => {
			if (Object.keys(lagreSoekRequestRef.current).length > 0) {
				DollyApi.lagreSoek(lagreSoekRequestRef.current, soekType.dolly)
					.then((response) => console.log(response))
					.catch((error) => console.error(error))
			}
		}
	}, [])

	const localStorageValue = localStorage.getItem(dollySoekLocalStorageKey)
	const initialValues = localStorageValue ? JSON.parse(localStorageValue) : dollySoekInitialValues

	const initialValuesClone = _.cloneDeep(initialValues)
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValuesClone,
	})

	const [formRequest, setFormRequest] = useState(initialValues)

	const setRequest = (request: any) => {
		localStorage.setItem(dollySoekLocalStorageKey, JSON.stringify(request))
		setFormRequest(request)
	}

	const { watch, reset } = formMethods
	const values = watch()

	const handleChange = (value: any, path: string, label: string) => {
		const updatedRequest = { ...values, side: 0, seed: null }
		_.set(updatedRequest, path, value)
		reset(updatedRequest)
		setRequest(updatedRequest)
		if (value) {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: {
					path: path,
					value: value,
					label: label,
				},
			})
		} else {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: undefined,
			})
		}
	}

	const handleChangeList = (value: any, path: string, label: string) => {
		const list = value?.map((item: any) => item.value ?? item)
		const updatedRequest = { ...values, [path]: list, side: 0, seed: null }
		reset(updatedRequest)
		setRequest(updatedRequest)
		if (value?.length > 0) {
			const request = value.map((i) => ({
				path: path,
				value: i.value ?? i,
				label: label?.includes(':') ? label : `${label}: ${i.label ?? codeToNorskLabel(i)}`,
			}))
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: request,
			})
		} else {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: [],
			})
		}
	}

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Dolly" />
				<Hjelpetekst placement={bottom}>
					Her kan du søke etter personer i Dolly ut fra ulike søkekriterier. Slik kan du gjenbruke
					eksisterende personer til nye formål.
				</Hjelpetekst>
			</div>
			<SisteSoek
				type={soekType.dolly}
				formValues={formMethods.watch()}
				handleChange={handleChange}
				handleChangeList={handleChangeList}
			/>
			<SoekForm
				formMethods={formMethods}
				localStorageValue={localStorageValue}
				handleChange={handleChange}
				handleChangeList={handleChangeList}
				setRequest={setRequest}
				formRequest={formRequest}
				lagreSoekRequest={lagreSoekRequest}
				setLagreSoekRequest={setLagreSoekRequest}
			/>
		</div>
	)
}
