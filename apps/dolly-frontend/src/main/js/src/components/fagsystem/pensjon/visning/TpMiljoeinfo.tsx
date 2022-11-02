import React from 'react'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import styled from 'styled-components'
import { TpVisningMiljoeinfo } from '~/components/fagsystem/pensjon/visning/TpVisningMiljoeinfo'

const Miljoeinfo = styled.div`
	margin-bottom: 10px;
	display: flex;
	flex-wrap: wrap;

	p {
		margin: 0;
	}
`

export const TpMiljoeinfo = ({ ident, bestilteMiljoer, ordninger }) => {
	const miljoer = useAsync(async () => {
		return DollyApi.getTpMiljoer()
	}, [])

	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '15px' }}>
			{miljoer && !miljoer?.loading ? (
				<Miljoeinfo>
					<TpVisningMiljoeinfo
						miljoer={miljoer?.value?.data}
						ident={ident}
						bestilteMiljoer={bestilteMiljoer}
						ordninger={ordninger}
					/>
					<p>
						<i>
							Hold pekeren over et miljø for å se dataene som finnes på denne personen i TP for det
							aktuelle miljøet.
						</i>
					</p>
				</Miljoeinfo>
			) : (
				<Loading label={'Laster miljøer...'} />
			)}
		</div>
	)
}
