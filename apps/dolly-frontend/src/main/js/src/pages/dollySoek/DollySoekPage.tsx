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

export default () => {
	const [lagreSoekRequest, setLagreSoekRequest] = useState({})
	const lagreSoekRequestRef = useRef(lagreSoekRequest)
	console.log('lagreSoekRequest: ', lagreSoekRequest) //TODO - SLETT MEG

	useEffect(() => {
		lagreSoekRequestRef.current = lagreSoekRequest
	}, [lagreSoekRequest])

	useEffect(() => {
		return () => {
			if (Object.keys(lagreSoekRequestRef.current).length > 0) {
				console.log('lagreSoekRequestRef.current: ', lagreSoekRequestRef.current) //TODO - SLETT MEG
				DollyApi.lagreSoek(lagreSoekRequestRef.current, 'DOLLY')
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

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Dolly" />
				<Hjelpetekst placement={bottom}>
					Her kan du søke etter personer i Dolly ut fra ulike søkekriterier. Slik kan du gjenbruke
					eksisterende personer til nye formål.
				</Hjelpetekst>
			</div>
			<SisteSoek soekType={soekType.dolly} />
			<SoekForm
				formMethods={formMethods}
				localStorageValue={localStorageValue}
				initialValues={initialValues}
				lagreSoekRequest={lagreSoekRequest}
				setLagreSoekRequest={setLagreSoekRequest}
			/>
		</div>
	)
}
