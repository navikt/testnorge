import Title from '../../components/title'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import React, { Suspense, useEffect, useRef, useState } from 'react'
import styled from 'styled-components'
import Loading from '@/components/ui/loading/Loading'
import { SisteSoek, soekType } from '@/components/ui/soekForm/SisteSoek'
import { useForm } from 'react-hook-form'
import { isDate } from 'date-fns'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { DollyApi } from '@/service/Api'
import { tenorSoekLocalStorageKey, tenorSoekStateLocalStorageKey } from './constants'
import { getLabel } from '@/components/ui/soekForm/utils'
import { Button } from '@navikt/ds-react'
import {
	ChevronDownDoubleIcon,
	ChevronUpDoubleIcon,
	ExclamationmarkTriangleIcon,
} from '@navikt/aksel-icons'

export { tenorSoekLocalStorageKey, tenorSoekStateLocalStorageKey }

const initialState = {
	personListe: [],
	side: 0,
	seed: null,
	nesteSide: null,
}

const NavigateButton = styled(Button)`
	position: fixed;
	bottom: 30px;
	left: 50%;
	transform: translateX(-50%);
	z-index: 1000;
	box-shadow: var(--ax-shadow-dialog);
	border-radius: var(--ax-radius-full);
	width: 12rem;

	.aksel-label {
		visibility: visible;
	}

	.aksel-loader {
		position: relative;
	}
`

export default () => {
	const [lagreSoekRequest, setLagreSoekRequest] = useState({})
	const lagreSoekRequestRef = useRef(lagreSoekRequest)

	useEffect(() => {
		lagreSoekRequestRef.current = lagreSoekRequest
	}, [lagreSoekRequest])

	useEffect(() => {
		return () => {
			if (Object.keys(lagreSoekRequestRef.current).length > 0) {
				DollyApi.lagreSoek(lagreSoekRequestRef.current, soekType.tenor)
					.then((response) => console.log(response))
					.catch((error) => console.error(error))
			}
		}
	}, [])

	const initialRequest = localStorage.getItem(tenorSoekLocalStorageKey)
		? JSON.parse(localStorage.getItem(tenorSoekLocalStorageKey) as string)
		: {}

	const initialStateValues = localStorage.getItem(tenorSoekStateLocalStorageKey)
		? JSON.parse(localStorage.getItem(tenorSoekStateLocalStorageKey) as string)
		: initialState

	const [formRequest, setFormRequest] = useState(initialRequest)
	const [state, setState] = useState<any>(initialStateValues)
	const { response, loading, error, mutate } = useTenorOversikt(
		formRequest,
		10,
		state.side,
		state.seed,
	)
	const [markertePersoner, setMarkertePersoner] = useState([])
	const [inkluderPartnere, setInkluderPartnere] = useState(false)

	const [overTreff, setOverTreff] = useState(false)

	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: formRequest || {},
	})

	const { setValue, watch }: any = formMethods

	useEffect(() => {
		setState(initialState)
	}, [formRequest])

	const setRequest = (request: any) => {
		setFormRequest(request)
		localStorage.setItem(tenorSoekLocalStorageKey, JSON.stringify(request))
	}

	useEffect(() => {
		if (response?.data?.data?.personer?.length === 0) {
			setState({
				...state,
				personListe: [],
				nesteSide: response?.data?.data?.nesteSide,
			})
		} else if (response && state.personListe?.length === 0) {
			setState({
				...state,
				personListe: response?.data?.data?.personer,
				seed: response?.data?.data?.seed,
				nesteSide: response?.data?.data?.nesteSide,
			})
		} else if (state.personListe?.length > 0 && response?.data?.data?.personer?.length > 0) {
			if (state.side > 0) {
				setState({
					...state,
					personListe: [
						...state.personListe,
						...response.data.data.personer.filter(
							(person) =>
								!state.personListe.some(
									(eksisterendePerson) => eksisterendePerson.id === person.id,
								),
						),
					],
					nesteSide: response?.data?.data?.nesteSide,
				})
			} else {
				setState({
					...state,
					personListe: response?.data?.data?.personer,
					nesteSide: response?.data?.data?.nesteSide,
				})
			}
		}
	}, [response])

	useEffect(() => {
		localStorage.setItem(tenorSoekStateLocalStorageKey, JSON.stringify(state))
	}, [state])

	useEffect(() => {
		window.addEventListener('scroll', handleScroll)
		return () => window.removeEventListener('scroll', handleScroll)
	}, [])

	useEffect(() => {
		const soekElement = document.getElementById('soek')
		if (!soekElement || typeof ResizeObserver === 'undefined') {
			return
		}
		const resizeObserver = new ResizeObserver(() => updateButtonPosition())
		resizeObserver.observe(soekElement)
		return () => resizeObserver.disconnect()
	}, [])

	const updateButtonPosition = () => {
		const treffElement = document.getElementById('treff')
		const soekElement = document.getElementById('soek')
		if (treffElement && soekElement) {
			const buttonPosition = window.scrollY + window.innerHeight - 30
			const soekTopVisible = soekElement.getBoundingClientRect().top >= 0
			setOverTreff(!soekTopVisible && buttonPosition >= treffElement.offsetTop)
		}
	}

	const handleScroll = () => {
		updateButtonPosition()
		if (
			document.documentElement.scrollHeight - document.documentElement.scrollTop >
			document.documentElement.clientHeight
		) {
			return
		}
		setState((state: any) => {
			if (state.side < 19 && state.nesteSide) {
				return { ...state, side: state.nesteSide }
			}
			return state
		})
	}

	const navigateTo = (element: string) => {
		const treff = document.getElementById(element)?.offsetTop
		window.scrollTo({ top: treff, behavior: 'smooth' })
	}

	function getUpdatedRequest(request: any) {
		for (let key of Object.keys(request)) {
			if (request[key] === '' || request[key] === null || request[key] === undefined) {
				delete request[key]
			} else if (typeof request[key] === 'object' && !(request[key] instanceof Date)) {
				request[key] = getUpdatedRequest(request[key])
				if (Object.keys(request[key]).length === 0) {
					delete request[key]
				} else {
					request[key] = getUpdatedRequest(request[key])
				}
			}
		}
		return Array.isArray(request) ? request.filter((val) => val) : request
	}

	const handleChange = (value: any, path: string, label: string) => {
		if (isDate(value)) {
			value = fixTimezone(value)
		}
		setValue(path, value)
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		setMarkertePersoner([])
		mutate()

		if (value || typeof value === 'boolean') {
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
		setValue(path, value)
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		setMarkertePersoner([])
		mutate()

		if (value?.length > 0) {
			const request = value.map((i) => ({
				path: path,
				value: i,
				label: getLabel(i, lagreSoekRequest, path, label),
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

	const emptyCategory = (paths: Array<string>) => {
		const lagreSoekRequestClone = { ...lagreSoekRequest }
		paths.forEach((path: string) => {
			setValue(path, undefined)
			delete lagreSoekRequestClone[path]
		})
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		setMarkertePersoner([])
		setLagreSoekRequest(lagreSoekRequestClone)
		mutate()
	}

	const ingenTreff = response?.data?.data?.treff < 1

	const getIcon = () => {
		if (ingenTreff) {
			return <ExclamationmarkTriangleIcon aria-hidden />
		} else if (overTreff) {
			return <ChevronUpDoubleIcon aria-hidden />
		} else {
			return <ChevronDownDoubleIcon aria-hidden />
		}
	}

	return (
		<Suspense fallback={<Loading label="Laster søkeside ..." panel />}>
			<div id="soek">
				<div className="flexbox--align-center--justify-start">
					<Title title="Søk etter personer i Tenor (Test-Norge)" />
				</div>
				<SisteSoek
					type={soekType.tenor}
					formValues={formMethods.watch()}
					handleChange={handleChange}
					handleChangeList={handleChangeList}
				/>
				<div className="flexbox--flex-wrap">
					<NavigateButton
						variant={loading || ingenTreff ? 'primary-neutral' : 'primary'}
						onClick={() => navigateTo(overTreff ? 'soek' : 'treff')}
						icon={loading ? null : getIcon()}
						loading={loading}
						disabled={loading || ingenTreff}
					>
						{loading
							? 'Henter treff ...'
							: ingenTreff
								? 'Ingen treff'
								: overTreff
									? 'Gå til søk'
									: 'Gå til treff'}
					</NavigateButton>
					<SoekForm
						formMethods={formMethods}
						handleChange={handleChange}
						handleChangeList={handleChangeList}
						emptyCategory={emptyCategory}
					/>
				</div>
			</div>
			<div id="treff">
				<TreffListe
					response={response?.data}
					personListe={state.personListe}
					markertePersoner={markertePersoner}
					setMarkertePersoner={setMarkertePersoner}
					inkluderPartnere={inkluderPartnere}
					setInkluderPartnere={setInkluderPartnere}
					nesteSide={state.nesteSide}
					loading={loading}
					error={error}
				/>
			</div>
		</Suspense>
	)
}
