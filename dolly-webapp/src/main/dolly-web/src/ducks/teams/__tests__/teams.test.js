import teams from '../index'
import { sokSelector } from '../index'

describe('teamsReducer', () => {
	const initialState = {
		items: [],
		visning: 'mine',
		visOpprettTeam: false,
		editTeamId: null
	}
	it('should return initial state', () => {
		expect(teams(undefined, {})).toEqual(initialState)
	})

	it('should handle success and add result array to items', () => {
		const testdata = ['test']
		const action = {
			type: 'teams/API/GET_SUCCESS',
			payload: { data: testdata }
		}

		const res = {
			items: testdata,
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(initialState, action)).toEqual(res)
	})

	it('should handle success and add single item to items', () => {
		const testdata = 'test'
		const action = {
			type: 'teams/API/GET_BY_ID_SUCCESS',
			payload: { data: testdata }
		}

		const res = {
			items: [testdata],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(initialState, action)).toEqual(res)
	})

	it('should handle create-success and add single item to items', () => {
		const testdata = 'test'
		const action = {
			type: 'teams/API/CREATE_SUCCESS',
			payload: { data: testdata }
		}

		const res = {
			items: [testdata],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(initialState, action)).toEqual(res)
	})

	it('should handle update-success and update existing item', () => {
		const testdata = { id: 1, value: 'test' }
		const action = {
			type: 'teams/API/UPDATE_SUCCESS',
			payload: { data: testdata }
		}

		const prevState = {
			items: [{ id: 1, value: 'beforeTest' }],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		const res = {
			items: [testdata],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(prevState, action)).toEqual(res)
	})

	it('should handle delete-success and remove from items', () => {
		const testdata = { id: 1, value: 'test' }
		const action = {
			type: 'teams/API/DELETE_SUCCESS',
			meta: { teamId: 1 }
		}

		const prevState = {
			items: [testdata],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		const res = {
			items: [],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(prevState, action)).toEqual(res)
	})

	it('it should set teamvisning', () => {
		const testdata = 'alle'
		const action = {
			type: 'teams/UI/SET_TEAM_VISNING',
			payload: { visning: testdata }
		}
		const res = {
			items: [],
			visning: testdata,
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(initialState, action)).toEqual(res)
	})

	it('should change start create team to TRUE', () => {
		const action = {
			type: 'teams/UI/START_CREATE_TEAM'
		}

		const res = {
			items: [],
			visning: 'mine',
			visOpprettTeam: true,
			editTeamId: null
		}

		expect(teams(initialState, action)).toEqual(res)
	})

	it('should change start edit team to TRUE', () => {
		const testdata = '2'

		const action = {
			type: 'teams/UI/START_EDIT_TEAM',
			payload: { teamId: testdata }
		}

		const res = {
			items: [],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: testdata
		}

		expect(teams(initialState, action)).toEqual(res)
	})

	it('close create or edit team', () => {
		const action = {
			type: 'teams/UI/CLOSE_CREATE_EDIT_TEAM'
		}
		const prevState = {
			items: [],
			visning: 'mine',
			visOpprettTeam: true,
			editTeamId: '1'
		}

		const res = {
			items: [],
			visning: 'mine',
			visOpprettTeam: false,
			editTeamId: null
		}

		expect(teams(prevState, action)).toEqual(res)
	})
})

describe('teamsReducer-sokSelector', () => {
	const testdata = [{ id: '1' }, { id: '2' }]
	const res = [{ id: '1' }]
	expect(sokSelector(testdata, '1')).toEqual(res)
})
