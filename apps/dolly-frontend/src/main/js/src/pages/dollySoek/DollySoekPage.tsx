import Title from '../../components/title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import {
	adressePath,
	dollySoekLocalStorageKey,
	personPath,
	SoekForm,
} from '@/pages/dollySoek/SoekForm'
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
	console.log('lagreSoekRequest: ', lagreSoekRequest) //TODO - SLETT MEG
	// TODO: Sett lagreSoekRequest til aa vaere det som evt ligger i form naar man kommer til siden?

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

	const { watch, reset, control, getValues } = formMethods
	const values = watch()

	const getLabel = (value: any, path: string) => {
		if (value === true) {
			return `${codeToNorskLabel(path)}`
		} else if (value.length > 3) {
			return `${codeToNorskLabel(path)}: ${codeToNorskLabel(value)}`
		}
		return `${codeToNorskLabel(path)}: ${value}`
	}

	const handleChange = (value: any, path: string) => {
		const updatedPersonRequest = { ...values.personRequest, [path]: value }
		const updatedRequest = { ...values, personRequest: updatedPersonRequest, side: 0, seed: null }
		reset(updatedRequest)
		setRequest(updatedRequest)
		if (value) {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: {
					path: `${personPath}.${path}`,
					value: value,
					label: getLabel(value, path),
				},
			})
		} else {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: undefined,
			})
		}
	}

	const handleChangeAdresse = (value: any, path: string) => {
		const updatedAdresseRequest = { ...values.personRequest.adresse, [path]: value }
		const updatedRequest = {
			...values,
			side: 0,
			seed: null,
		}
		_.set(updatedRequest, 'personRequest.adresse', updatedAdresseRequest)
		reset(updatedRequest)
		setRequest(updatedRequest)
		if (value) {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: {
					path: `${adressePath}.${path}`,
					value: value,
					label: getLabel(value, path),
				},
			})
		} else {
			setLagreSoekRequest({
				...lagreSoekRequest,
				[path]: undefined,
			})
		}
	}

	const handleChangeList = (value: any, path: string) => {
		const list = value?.map((item: any) => item.value || item)
		const updatedRequest = { ...values, [path]: list, side: 0, seed: null }
		reset(updatedRequest)
		setRequest(updatedRequest)
		if (path === 'registreRequest') {
			if (value?.length > 0) {
				const fagsystemer = value.map((system) => ({
					path: 'registreRequest',
					value: system.value,
					label: `Fagsystem: ${system.label}`,
				}))
				setLagreSoekRequest({
					...lagreSoekRequest,
					registreRequest: fagsystemer,
				})
			} else {
				setLagreSoekRequest({
					...lagreSoekRequest,
					registreRequest: [],
				})
			}
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
				soekType={soekType.dolly}
				formValues={formMethods.watch()}
				handleChange={handleChange}
				handleChangeAdresse={handleChangeAdresse}
				handleChangeList={handleChangeList}
			/>
			<SoekForm
				formMethods={formMethods}
				localStorageValue={localStorageValue}
				handleChange={handleChange}
				handleChangeAdresse={handleChangeAdresse}
				handleChangeList={handleChangeList}
				setRequest={setRequest}
				formRequest={formRequest}
				lagreSoekRequest={lagreSoekRequest}
				setLagreSoekRequest={setLagreSoekRequest}
			/>
		</div>
	)
}
