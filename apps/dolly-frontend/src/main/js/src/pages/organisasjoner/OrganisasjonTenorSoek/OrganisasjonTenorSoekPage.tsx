import Title from '../../../components/title'
import { useTenorOversiktOrganisasjoner } from '@/utils/hooks/useTenorSoek'
import React, { useEffect, useState } from 'react'
import { TreffListeOrg } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/TreffListeOrg'
import { SoekFormOrg } from '@/pages/organisasjoner/OrganisasjonTenorSoek/SoekFormOrg'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'

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

const initialState = {
	organisasjonListe: [],
	side: 0,
	seed: null,
	nesteSide: null,
}

export default () => {
	const [request, setRequest] = useState({})
	const [state, setState] = useState<any>(initialState)
	const { response, loading, error, mutate } = useTenorOversiktOrganisasjoner(
		request,
		10,
		state.side,
		state.seed,
	)

	useEffect(() => {
		if (response) {
			const organisasjoner = response.data?.data?.organisasjoner || []
			const nextState = {
				...state,
				organisasjonListe:
					state.side > 0 ? [...state.organisasjonListe, ...organisasjoner] : organisasjoner,
				seed: response.data?.data?.seed,
				nesteSide: response.data?.data?.nesteSide,
			}
			setState(nextState)
		}
	}, [response])

	useEffect(() => {
		window.addEventListener('scroll', handleScroll)
		return () => window.removeEventListener('scroll', handleScroll)
	}, [])

	const navigateTo = (element: string) => {
		const treff = document.getElementById(element)?.offsetTop
		window.scrollTo({ top: treff, behavior: 'smooth' })
	}

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

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter organisasjoner i Tenor (Test-Norge)" />
			</div>
			<div className="flexbox--flex-wrap" id="soek">
				<NavigateButton
					className="gaa-til-treff"
					onClick={() => navigateTo('treff')}
					kind="chevron-down-double-circle"
				>
					GÅ TIL TREFF
				</NavigateButton>
				<SoekFormOrg setRequest={setRequest} mutate={mutate} />
			</div>
			<div id="treff">
				<NavigateButton
					className="gaa-til-soek"
					onClick={() => navigateTo('soek')}
					kind="chevron-up-double-circle"
				>
					GÅ TIL SØK
				</NavigateButton>
				<TreffListeOrg
					response={response?.data}
					organisasjonListe={state.organisasjonListe}
					loading={loading}
					error={error}
				/>
			</div>
		</div>
	)
}
