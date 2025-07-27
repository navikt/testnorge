import Title from '../../components/title'
import { useTenorOversikt } from '@/utils/hooks/useTenorSoek'
import { SoekForm } from '@/pages/tenorSoek/SoekForm'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'

const initialState = {
	personListe: [],
	side: 0,
	seed: null,
	nesteSide: null,
}

const NavigateButton = styled(Button)`
	position: sticky;
	top: ${(props) => (props.className === 'gaa-til-soek' ? '75px' : '10px')};
	width: 80px;
	transform: translateX(-120%);
	display: grid;

	&& {
		svg {
			width: 45px;
			height: 45px;
			margin: 0 auto 5px auto;
		}
	}
`

export const tenorSoekLocalStorageKey = 'tenorSoek'
export const tenorSoekStateLocalStorageKey = 'tenorSoekState'

export default () => {
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

	const handleScroll = () => {
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

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor (Test-Norge)" />
			</div>
			<div className="flexbox--flex-wrap" id="soek">
				<NavigateButton
					className="gaa-til-treff"
					onClick={() => navigateTo('treff')}
					kind="chevron-down-double-circle"
				>
					GÅ TIL TREFF
				</NavigateButton>
				<SoekForm
					request={formRequest}
					setRequest={setRequest}
					setMarkertePersoner={setMarkertePersoner}
					mutate={mutate}
				/>
			</div>
			<div id="treff">
				<NavigateButton
					className="gaa-til-soek"
					onClick={() => navigateTo('soek')}
					kind="chevron-up-double-circle"
				>
					GÅ TIL SØK
				</NavigateButton>
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
		</div>
	)
}
