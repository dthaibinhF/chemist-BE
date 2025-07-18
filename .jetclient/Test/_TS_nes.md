```toml
name = 'Group Test Script'
sortWeight = 500000
id = 'group-test-script'
```

#### Script

```js
/**
 * Group Test Script
 * 
 * This script tests all endpoints in the GroupController:
 * - POST /api/v1/group - Create a new group
 * - GET /api/v1/group/{id} - Get a group by ID
 * - GET /api/v1/group - Get all groups
 * - GET /api/v1/group/detail - Get all groups with detail
 * - GET /api/v1/group/academic-year/{academicYearId} - Get groups by academic year ID
 * - GET /api/v1/group/grade/{gradeId} - Get groups by grade ID
 * - PUT /api/v1/group/{id} - Update a group
 * - DELETE /api/v1/group/{id} - Delete a group
 */

// First, login to get the authentication token
jc.runRequest("/Auth/Login")

// Test case for Group CRUD operations
jc.testCase("Group CRUD Operations", function () {
    // Setup: Create dependencies (Fee, AcademicYear, Grade)
    console.log("Setting up dependencies...")
    
    // Create Fee
    jc.runRequest("/Fee/Create fee")
    const feeId = jc.variables.get("feeId")
    console.log("Created Fee with ID:", feeId)
    
    // Create Academic Year
    jc.runRequest("/Academi-year/Create academic year")
    const academicYearId = jc.variables.get("academicYearId")
    console.log("Created Academic Year with ID:", academicYearId)
    
    // Create Grade
    jc.runRequest("/Grade/Create grade")
    const gradeId = jc.variables.get("gradeId")
    console.log("Created Grade with ID:", gradeId)
    
    // Create Room for group schedule
    jc.runRequest("/Room/Create room")
    const roomId = jc.variables.get("roomId")
    console.log("Created Room with ID:", roomId)
    
    // 1. Test creating a new group
    console.log("Testing POST /api/v1/group")
    
    // Create a new HTTP request
    const createGroupRequest = new jc.Request({
        method: "POST",
        url: "{{baseUrl}}/api/v1/group",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "{{access_token}}"
        },
        body: {
            name: "Test Physics Group",
            level: "Advanced",
            fee_id: feeId,
            academic_year_id: academicYearId,
            grade_id: gradeId,
            group_schedules: [
                {
                    day_of_week: "MONDAY",
                    start_time: "2025-07-17T09:00:00Z",
                    end_time: "2025-07-17T11:00:00Z",
                    room_id: roomId
                }
            ]
        }
    })
    
    // Send the request and store the response
    const createResponse = createGroupRequest.send()
    
    // Test the response
    jc.test("Create group - status code is 200", function() {
        createResponse.to.have.status(200)
    })
    
    jc.test("Create group - response has correct name", function() {
        createResponse.json("name").to.equal("Test Physics Group")
    })
    
    jc.test("Create group - response has correct level", function() {
        createResponse.json("level").to.equal("Advanced")
    })
    
    jc.test("Create group - response has correct fee_id", function() {
        createResponse.json("fee_id").to.equal(parseInt(feeId))
    })
    
    jc.test("Create group - response has correct academic_year_id", function() {
        createResponse.json("academic_year_id").to.equal(parseInt(academicYearId))
    })
    
    jc.test("Create group - response has correct grade_id", function() {
        createResponse.json("grade_id").to.equal(parseInt(gradeId))
    })
    
    jc.test("Create group - response has group_schedules", function() {
        createResponse.json("group_schedules").to.be.an("array").that.is.not.empty
    })
    
    // Store the group ID for later use
    const groupId = createResponse.json("id")
    jc.variables.set("groupId", groupId)
    console.log("Created Group with ID:", groupId)
    
    // 2. Test getting a group by ID
    console.log("Testing GET /api/v1/group/{id}")
    
    const getGroupRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group/" + groupId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getResponse = getGroupRequest.send()
    
    jc.test("Get group by ID - status code is 200", function() {
        getResponse.to.have.status(200)
    })
    
    jc.test("Get group by ID - response has correct ID", function() {
        getResponse.json("id").to.equal(groupId)
    })
    
    jc.test("Get group by ID - response has correct name", function() {
        getResponse.json("name").to.equal("Test Physics Group")
    })
    
    // 3. Test getting all groups
    console.log("Testing GET /api/v1/group")
    
    const getAllGroupsRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group",
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getAllResponse = getAllGroupsRequest.send()
    
    jc.test("Get all groups - status code is 200", function() {
        getAllResponse.to.have.status(200)
    })
    
    jc.test("Get all groups - response is an array", function() {
        getAllResponse.json().to.be.an("array")
    })
    
    jc.test("Get all groups - response contains the created group", function() {
        const groups = getAllResponse.json()
        const found = groups.some(group => group.id === groupId)
        jc.expect(found).to.be.true
    })
    
    // 4. Test getting all groups with detail
    console.log("Testing GET /api/v1/group/detail")
    
    const getAllDetailRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group/detail",
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getAllDetailResponse = getAllDetailRequest.send()
    
    jc.test("Get all groups with detail - status code is 200", function() {
        getAllDetailResponse.to.have.status(200)
    })
    
    jc.test("Get all groups with detail - response is an array", function() {
        getAllDetailResponse.json().to.be.an("array")
    })
    
    jc.test("Get all groups with detail - response contains the created group", function() {
        const groups = getAllDetailResponse.json()
        const found = groups.some(group => group.id === groupId)
        jc.expect(found).to.be.true
    })
    
    // 5. Test getting groups by academic year ID
    console.log("Testing GET /api/v1/group/academic-year/{academicYearId}")
    
    const getByAcademicYearRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group/academic-year/" + academicYearId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getByAcademicYearResponse = getByAcademicYearRequest.send()
    
    jc.test("Get groups by academic year ID - status code is 200", function() {
        getByAcademicYearResponse.to.have.status(200)
    })
    
    jc.test("Get groups by academic year ID - response is an array", function() {
        getByAcademicYearResponse.json().to.be.an("array")
    })
    
    jc.test("Get groups by academic year ID - response contains the created group", function() {
        const groups = getByAcademicYearResponse.json()
        const found = groups.some(group => group.id === groupId)
        jc.expect(found).to.be.true
    })
    
    // 6. Test getting groups by grade ID
    console.log("Testing GET /api/v1/group/grade/{gradeId}")
    
    const getByGradeRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group/grade/" + gradeId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getByGradeResponse = getByGradeRequest.send()
    
    jc.test("Get groups by grade ID - status code is 200", function() {
        getByGradeResponse.to.have.status(200)
    })
    
    jc.test("Get groups by grade ID - response is an array", function() {
        getByGradeResponse.json().to.be.an("array")
    })
    
    jc.test("Get groups by grade ID - response contains the created group", function() {
        const groups = getByGradeResponse.json()
        const found = groups.some(group => group.id === groupId)
        jc.expect(found).to.be.true
    })
    
    // 7. Test updating a group
    console.log("Testing PUT /api/v1/group/{id}")
    
    const updateGroupRequest = new jc.Request({
        method: "PUT",
        url: "{{baseUrl}}/api/v1/group/" + groupId,
        headers: {
            "Content-Type": "application/json",
            "Authorization": "{{access_token}}"
        },
        body: {
            name: "Updated Physics Group",
            level: "Intermediate",
            fee_id: feeId,
            academic_year_id: academicYearId,
            grade_id: gradeId,
            group_schedules: [
                {
                    id: createResponse.json("group_schedules[0].id"),
                    day_of_week: "TUESDAY", // Changed from MONDAY
                    start_time: "2025-07-17T10:00:00Z", // Changed time
                    end_time: "2025-07-17T12:00:00Z", // Changed time
                    room_id: roomId
                }
            ]
        }
    })
    
    const updateResponse = updateGroupRequest.send()
    
    jc.test("Update group - status code is 200", function() {
        updateResponse.to.have.status(200)
    })
    
    jc.test("Update group - response has correct updated name", function() {
        updateResponse.json("name").to.equal("Updated Physics Group")
    })
    
    jc.test("Update group - response has correct updated level", function() {
        updateResponse.json("level").to.equal("Intermediate")
    })
    
    jc.test("Update group - response has updated group_schedules", function() {
        updateResponse.json("group_schedules[0].day_of_week").to.equal("TUESDAY")
    })
    
    // Verify the update by getting the group again
    const getUpdatedGroupRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group/" + groupId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getUpdatedResponse = getUpdatedGroupRequest.send()
    
    jc.test("Get updated group - name is updated", function() {
        getUpdatedResponse.json("name").to.equal("Updated Physics Group")
    })
    
    jc.test("Get updated group - level is updated", function() {
        getUpdatedResponse.json("level").to.equal("Intermediate")
    })
    
    // 8. Test deleting a group
    console.log("Testing DELETE /api/v1/group/{id}")
    
    const deleteGroupRequest = new jc.Request({
        method: "DELETE",
        url: "{{baseUrl}}/api/v1/group/" + groupId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const deleteResponse = deleteGroupRequest.send()
    
    jc.test("Delete group - status code is 204", function() {
        deleteResponse.to.have.status(204)
    })
    
    // Verify the deletion by trying to get the group again
    const getDeletedGroupRequest = new jc.Request({
        method: "GET",
        url: "{{baseUrl}}/api/v1/group/" + groupId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    
    const getDeletedResponse = getDeletedGroupRequest.send()
    
    jc.test("Get deleted group - status code is 404", function() {
        getDeletedResponse.to.have.status(404)
    })
    
    // Clean up: Delete the dependencies
    console.log("Cleaning up dependencies...")
    
    // Delete Room
    const deleteRoomRequest = new jc.Request({
        method: "DELETE",
        url: "{{baseUrl}}/api/v1/room/" + roomId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    deleteRoomRequest.send()
    
    // Delete Grade
    const deleteGradeRequest = new jc.Request({
        method: "DELETE",
        url: "{{baseUrl}}/api/v1/grade/" + gradeId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    deleteGradeRequest.send()
    
    // Delete Academic Year
    const deleteAcademicYearRequest = new jc.Request({
        method: "DELETE",
        url: "{{baseUrl}}/api/v1/academic-year/" + academicYearId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    deleteAcademicYearRequest.send()
    
    // Delete Fee
    const deleteFeeRequest = new jc.Request({
        method: "DELETE",
        url: "{{baseUrl}}/api/v1/fee/" + feeId,
        headers: {
            "Authorization": "{{access_token}}"
        }
    })
    deleteFeeRequest.send()
})
```         